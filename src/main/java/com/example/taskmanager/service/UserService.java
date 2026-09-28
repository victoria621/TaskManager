package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserRequest;
import com.example.taskmanager.dto.UserResponse;
import com.example.taskmanager.entity.UserEntity;
import com.example.taskmanager.exception.ResourceAlreadyExistsException;
import com.example.taskmanager.exception.ResourceCannotBeDeletedException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final String RESOURCE_NAME = "User";

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse createUser(UserRequest requestDTO) {
        log.info("Creating user with email: {}", requestDTO.email());

        if (userRepository.existsByEmail(requestDTO.email())) {
            log.warn("User with email {} already exists", requestDTO.email());
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.email());
        }

        var user = userMapper.toEntity(requestDTO);
        user.setActive(true);

        var saved = userRepository.save(user);
        log.info("User created successfully with id: {}", saved.getId());

        return userMapper.toDto(saved);
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(Long id) {
        log.info("Fetching user from database with id: {}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException(RESOURCE_NAME, id);
                });

        return userMapper.toDto(user);
    }

    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user from database with email: {}", email);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new ResourceNotFoundException(RESOURCE_NAME, email);
                });

        return userMapper.toDto(user);
    }

    @Cacheable(value = "users", key = "'all'")
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users from database");

        List<UserResponse> users = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} users", users.size());
        return users;
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse updateUser(Long id, UserRequest requestDTO) {
        log.info("Updating user with id: {}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!user.getEmail().equals(requestDTO.email()) &&
                userRepository.existsByEmail(requestDTO.email())) {
            log.warn("Email {} already in use", requestDTO.email());
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.email());
        }

        user.setName(requestDTO.name());
        user.setSurname(requestDTO.surname());
        user.setEmail(requestDTO.email());

        var updated = userRepository.save(user);
        log.info("User {} updated successfully", id);

        return userMapper.toDto(updated);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse updateUserPassword(Long id, String newPassword) {
        log.info("Updating password for user: {}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        user.setPassword(newPassword);
        var updated = userRepository.save(user);

        log.info("Password updated for user: {}", id);
        return userMapper.toDto(updated);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!user.getTasks().isEmpty()) {
            log.warn("Cannot delete user {} - has {} tasks", id, user.getTasks().size());
            throw new ResourceCannotBeDeletedException(RESOURCE_NAME, "user has existing tasks");
        }

        userRepository.delete(user);
        log.info("User {} deleted successfully", id);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user: {}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        user.setActive(true);
        userRepository.save(user);

        log.info("User {} activated", id);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user: {}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        user.setActive(false);
        userRepository.save(user);

        log.info("User {} deactivated", id);
    }

    public UserEntity findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}
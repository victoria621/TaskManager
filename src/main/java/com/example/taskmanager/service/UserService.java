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

    @Transactional
    public UserResponse createUser(UserRequest requestDTO) {
        log.info("Creating user with email: {}", requestDTO.email());

        if (userRepository.existsByEmail(requestDTO.email())) {
            log.warn("User with email {} already exists", requestDTO.email());
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.email());
        }

        UserEntity user = userMapper.toEntity(requestDTO);
        user.setActive(true);

        UserEntity saved = userRepository.save(user);
        log.info("User created successfully with id: {}", saved.getId());

        return userMapper.toDto(saved);
    }

    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException(RESOURCE_NAME, id);
                });

        return userMapper.toDto(user);
    }

    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new ResourceNotFoundException(RESOURCE_NAME, email);
                });

        return userMapper.toDto(user);
    }

    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");

        List<UserResponse> users = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} users", users.size());
        return users;
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest requestDTO) {
        log.info("Updating user with id: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!user.getEmail().equals(requestDTO.email()) &&
                userRepository.existsByEmail(requestDTO.email())) {
            log.warn("Email {} already in use", requestDTO.email());
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.email());
        }

        user.setName(requestDTO.name());
        user.setSurname(requestDTO.surname());
        user.setEmail(requestDTO.email());

        UserEntity updated = userRepository.save(user);
        log.info("User {} updated successfully", id);

        return userMapper.toDto(updated);
    }

    @Transactional
    public UserResponse updateUserPassword(Long id, String newPassword) {
        log.info("Updating password for user: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        user.setPassword(newPassword);
        UserEntity updated = userRepository.save(user);

        log.info("Password updated for user: {}", id);
        return userMapper.toDto(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!user.getTasks().isEmpty()) {
            log.warn("Cannot delete user {} - has {} tasks", id, user.getTasks().size());
            throw new ResourceCannotBeDeletedException(RESOURCE_NAME, "user has existing tasks");
        }

        userRepository.delete(user);
        log.info("User {} deleted successfully", id);
    }

    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        user.setActive(true);
        userRepository.save(user);

        log.info("User {} activated", id);
    }

    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user: {}", id);

        UserEntity user = userRepository.findById(id)
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
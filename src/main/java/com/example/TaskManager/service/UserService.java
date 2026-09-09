package com.example.TaskManager.service;

import com.example.TaskManager.dto.UserRequest;
import com.example.TaskManager.dto.UserResponse;
import com.example.TaskManager.entity.UserEntity;
import com.example.TaskManager.exception.ResourceAlreadyExistsException;
import com.example.TaskManager.exception.ResourceCannotBeDeletedException;
import com.example.TaskManager.exception.ResourceNotFoundException;
import com.example.TaskManager.mapper.UserMapper;
import com.example.TaskManager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.email());
        }

        UserEntity user = userMapper.toEntity(requestDTO);
        user.setActive(true);

        return userMapper.toDto(userRepository.save(user));
    }

    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        return userMapper.toDto(user);
    }

    public UserResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, email));
        return userMapper.toDto(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest requestDTO) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!user.getEmail().equals(requestDTO.email()) &&
                userRepository.existsByEmail(requestDTO.email())) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.email());
        }

        user.setName(requestDTO.name());
        user.setSurname(requestDTO.surname());
        user.setEmail(requestDTO.email());

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUserPassword(Long id, String newPassword) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        user.setPassword(newPassword);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!user.getTasks().isEmpty()) {
            throw new ResourceCannotBeDeletedException(RESOURCE_NAME, "user has existing tasks");
        }

        userRepository.delete(user);
    }

    @Transactional
    public void activateUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        user.setActive(false);
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserEntity findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}
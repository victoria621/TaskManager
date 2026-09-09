package com.example.TaskManager.service;

import com.example.TaskManager.dto.TaskRequest;
import com.example.TaskManager.dto.TaskResponse;
import com.example.TaskManager.entity.CategoryEntity;
import com.example.TaskManager.entity.TaskEntity;
import com.example.TaskManager.entity.TaskStatus;
import com.example.TaskManager.entity.UserEntity;
import com.example.TaskManager.exception.ResourceNotFoundException;
import com.example.TaskManager.mapper.TaskMapper;
import com.example.TaskManager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private static final String RESOURCE_NAME = "Task";

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper,
                       UserService userService, CategoryService categoryService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Transactional
    public TaskResponse createTask(TaskRequest requestDTO, Long userId) {
        UserEntity user = userService.findEntityById(userId);
        CategoryEntity category = categoryService.findEntityById(requestDTO.categoryId());

        TaskEntity task = taskMapper.toEntity(requestDTO);
        task.setUser(user);
        task.setCategory(category);

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }

        return taskMapper.toDto(taskRepository.save(task));
    }

    public TaskResponse getTaskById(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        return taskMapper.toDto(task);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByUser(Long userId) {
        userService.findEntityById(userId);
        return taskRepository.findByUserId(userId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByUserAndCategory(Long userId, Long categoryId) {
        userService.findEntityById(userId);
        categoryService.findEntityById(categoryId);
        return taskRepository.findByUserIdAndCategoryId(userId, categoryId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByUserAndStatus(Long userId, TaskStatus status) {
        userService.findEntityById(userId);
        return taskRepository.findByUserIdAndStatus(userId, status).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest requestDTO) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (requestDTO.categoryId() != null) {
            CategoryEntity category = categoryService.findEntityById(requestDTO.categoryId());
            task.setCategory(category);
        }

        task.setTitle(requestDTO.title());
        task.setDescription(requestDTO.description());
        task.setDueDate(requestDTO.dueDate());

        if (requestDTO.status() != null) {
            task.setStatus(requestDTO.status());
        }

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus status) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        task.setStatus(status);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        taskRepository.delete(task);
    }

    public TaskEntity findEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}
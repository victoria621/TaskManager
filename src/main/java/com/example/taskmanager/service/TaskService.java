package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.CategoryEntity;
import com.example.taskmanager.entity.TaskEntity;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.UserEntity;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.strategy.TaskValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TaskValidationService taskValidationService;
    private static final String RESOURCE_NAME = "Task";

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper,
                       UserService userService, CategoryService categoryService, TaskValidationService taskValidationService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userService = userService;
        this.categoryService = categoryService;
        this.taskValidationService = taskValidationService;
    }

    @Transactional
    public TaskResponse createTask(TaskRequest requestDTO, Long userId) {
        log.info("Creating task '{}' for user {}", requestDTO.title(), userId);

        UserEntity user = userService.findEntityById(userId);
        CategoryEntity category = categoryService.findEntityById(requestDTO.categoryId());

        TaskEntity task = taskMapper.toEntity(requestDTO);
        task.setUser(user);
        task.setCategory(category);

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        taskValidationService.validate(task);

        TaskEntity saved = taskRepository.save(task);
        log.info("Task created with id: {}", saved.getTaskId());

        return taskMapper.toDto(saved);
    }

    public TaskResponse getTaskById(Long id) {
        log.debug("Fetching task with id: {}", id);

        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new ResourceNotFoundException(RESOURCE_NAME, id);
                });

        return taskMapper.toDto(task);
    }

    public List<TaskResponse> getAllTasks() {
        log.debug("Fetching all tasks");

        List<TaskResponse> tasks = taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} tasks", tasks.size());
        return tasks;
    }

    public List<TaskResponse> getTasksByUser(Long userId) {
        log.debug("Fetching tasks for user: {}", userId);

        userService.findEntityById(userId);

        List<TaskResponse> tasks = taskRepository.findByUserId(userId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} tasks for user {}", tasks.size(), userId);
        return tasks;
    }

    public List<TaskResponse> getTasksByUserAndCategory(Long userId, Long categoryId) {
        log.debug("Fetching tasks for user {} and category {}", userId, categoryId);

        userService.findEntityById(userId);
        categoryService.findEntityById(categoryId);

        List<TaskResponse> tasks = taskRepository.findByUserIdAndCategoryCategoryId(userId, categoryId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} tasks for user {} in category {}", tasks.size(), userId, categoryId);
        return tasks;
    }

    public List<TaskResponse> getTasksByUserAndStatus(Long userId, TaskStatus status) {
        log.debug("Fetching tasks for user {} with status {}", userId, status);

        userService.findEntityById(userId);

        List<TaskResponse> tasks = taskRepository.findByUserIdAndStatus(userId, status).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} tasks for user {} with status {}", tasks.size(), userId, status);
        return tasks;
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest requestDTO) {
        log.info("Updating task with id: {}", id);

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

        taskValidationService.validate(task);

        TaskEntity updated = taskRepository.save(task);
        log.info("Task {} updated successfully", id);

        return taskMapper.toDto(updated);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus status) {
        log.info("Updating status of task {} to {}", id, status);

        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(status);

        TaskEntity updated = taskRepository.save(task);
        log.info("Task {} status changed from {} to {}", id, oldStatus, status);

        return taskMapper.toDto(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);

        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        taskRepository.delete(task);
        log.info("Task {} deleted successfully", id);
    }
}
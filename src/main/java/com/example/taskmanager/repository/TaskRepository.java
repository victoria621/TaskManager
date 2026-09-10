package com.example.taskmanager.repository;

import com.example.taskmanager.entity.TaskEntity;
import com.example.taskmanager.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity,Long> {
    List<TaskEntity> findByUserId(Long userId);

    List<TaskEntity> findByUserIdAndCategoryCategoryId(Long userId, Long categoryId);

    List<TaskEntity> findByUserIdAndStatus(Long userId, TaskStatus status);
}

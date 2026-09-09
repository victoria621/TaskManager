package com.example.TaskManager.repository;

import com.example.TaskManager.entity.TaskEntity;
import com.example.TaskManager.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity,Long> {
    List<TaskEntity> findByUserId(Long userId);

    List<TaskEntity> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<TaskEntity> findByUserIdAndStatus(Long userId, TaskStatus status);
}

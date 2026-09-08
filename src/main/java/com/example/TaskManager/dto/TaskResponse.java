package com.example.TaskManager.dto;

import com.example.TaskManager.entity.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long taskId,
        String title,
        String description,
        LocalDateTime dueDate,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        TaskStatus status,
        UserResponse user,
        CategoryResponse category
) {
}

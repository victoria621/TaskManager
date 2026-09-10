package com.example.taskmanager.dto;

import com.example.taskmanager.entity.TaskStatus;

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

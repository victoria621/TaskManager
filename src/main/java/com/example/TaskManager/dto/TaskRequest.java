package com.example.TaskManager.dto;

import com.example.TaskManager.entity.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
        String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date must be in the present or future")
        LocalDateTime dueDate,

        TaskStatus status,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}
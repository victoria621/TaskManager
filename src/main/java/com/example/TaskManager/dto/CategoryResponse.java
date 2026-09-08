package com.example.TaskManager.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryResponse(
        Long categoryId,
        String categoryName,
        String categoryDescription,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<TaskResponse> tasks
) {
}

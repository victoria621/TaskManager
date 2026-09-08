package com.example.TaskManager.dto;


import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String surname,
        String email,
        Boolean active,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        List<TaskResponse> tasks
) {
}

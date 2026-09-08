package com.example.TaskManager.mapper;

import com.example.TaskManager.dto.CategoryResponse;
import com.example.TaskManager.dto.TaskRequest;
import com.example.TaskManager.dto.TaskResponse;
import com.example.TaskManager.dto.UserResponse;
import com.example.TaskManager.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected CategoryMapper categoryMapper;

    @Mappings({
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "category", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "taskId", ignore = true)
    })
    public abstract TaskEntity toEntity(TaskRequest dto);

    public TaskResponse toDto(TaskEntity entity) {
        if (entity == null) {
            return null;
        }

        UserResponse userResponse = null;
        if (entity.getUser() != null) {
            userResponse = userMapper.toDto(entity.getUser());
        }

        CategoryResponse categoryResponse = null;
        if (entity.getCategory() != null) {
            categoryResponse = categoryMapper.toDto(entity.getCategory());
        }

        return new TaskResponse(
                entity.getTaskId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDueDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getStatus(),
                userResponse,
                categoryResponse
        );
    }
}
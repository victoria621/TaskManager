package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {

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

        return new TaskResponse(
                entity.getTaskId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDueDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getStatus(),
                null,
                null
        );
    }
}
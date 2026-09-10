package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.dto.UserRequest;
import com.example.taskmanager.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserRequest dto);
    @Mappings({
            @Mapping(target = "tasks", ignore = true)
    })
    UserResponse toDto(UserEntity entity);
}

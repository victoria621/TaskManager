package com.example.TaskManager.mapper;

import com.example.TaskManager.dto.*;
import com.example.TaskManager.dto.UserRequest;
import com.example.TaskManager.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserRequest dto);
    UserResponse toDto(UserEntity entity);
}

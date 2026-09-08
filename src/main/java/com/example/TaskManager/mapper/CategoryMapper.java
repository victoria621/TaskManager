package com.example.TaskManager.mapper;

import com.example.TaskManager.dto.CategoryRequest;
import com.example.TaskManager.dto.CategoryResponse;
import com.example.TaskManager.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toEntity(CategoryRequest dto);
    CategoryResponse toDto(CategoryEntity entity);
}

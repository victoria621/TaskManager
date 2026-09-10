package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.CategoryRequest;
import com.example.taskmanager.dto.CategoryResponse;
import com.example.taskmanager.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toEntity(CategoryRequest dto);
    @Mappings({
            @Mapping(target = "tasks", ignore = true)
    })
    CategoryResponse toDto(CategoryEntity entity);
}

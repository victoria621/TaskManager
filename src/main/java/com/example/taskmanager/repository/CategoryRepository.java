package com.example.taskmanager.repository;

import com.example.taskmanager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsByCategoryName(String categoryName);
    Optional<CategoryEntity> findByCategoryName(String categoryName);
}
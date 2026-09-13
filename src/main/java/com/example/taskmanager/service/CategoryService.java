package com.example.taskmanager.service;

import com.example.taskmanager.dto.CategoryRequest;
import com.example.taskmanager.dto.CategoryResponse;
import com.example.taskmanager.entity.CategoryEntity;
import com.example.taskmanager.exception.ResourceAlreadyExistsException;
import com.example.taskmanager.exception.ResourceCannotBeDeletedException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.mapper.CategoryMapper;
import com.example.taskmanager.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private static final String RESOURCE_NAME = "Category";

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest requestDTO) {
        log.info("Creating category with name: {}", requestDTO.categoryName());

        if (categoryRepository.existsByCategoryName(requestDTO.categoryName())) {
            log.warn("Category {} already exists", requestDTO.categoryName());
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.categoryName());
        }

        CategoryEntity category = categoryMapper.toEntity(requestDTO);
        category.setActive(true);

        CategoryEntity saved = categoryRepository.save(category);
        log.info("Category created with id: {}", saved.getCategoryId());

        return categoryMapper.toDto(saved);
    }

    public CategoryResponse getCategoryById(Long id) {
        log.debug("Fetching category with id: {}", id);

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", id);
                    return new ResourceNotFoundException(RESOURCE_NAME, id);
                });

        return categoryMapper.toDto(category);
    }

    public CategoryResponse getCategoryByName(String name) {
        log.debug("Fetching category with name: {}", name);

        CategoryEntity category = categoryRepository.findByCategoryName(name)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, name));

        return categoryMapper.toDto(category);
    }

    public List<CategoryResponse> getAllCategories() {
        log.debug("Fetching all categories");

        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} categories", categories.size());
        return categories;
    }

    public List<CategoryResponse> getActiveCategories() {
        log.debug("Fetching active categories");

        return categoryRepository.findAll().stream()
                .filter(CategoryEntity::getActive)
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest requestDTO) {
        log.info("Updating category with id: {}", id);

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!category.getCategoryName().equals(requestDTO.categoryName()) &&
                categoryRepository.existsByCategoryName(requestDTO.categoryName())) {
            log.warn("Category name {} already exists", requestDTO.categoryName());
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.categoryName());
        }

        category.setCategoryName(requestDTO.categoryName());
        category.setCategoryDescription(requestDTO.categoryDescription());

        CategoryEntity updated = categoryRepository.save(category);
        log.info("Category {} updated successfully", id);

        return categoryMapper.toDto(updated);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!category.getTasks().isEmpty()) {
            log.warn("Cannot delete category {} - has {} tasks", id, category.getTasks().size());
            throw new ResourceCannotBeDeletedException(RESOURCE_NAME, "category has existing tasks");
        }

        categoryRepository.delete(category);
        log.info("Category {} deleted successfully", id);
    }

    @Transactional
    public void activateCategory(Long id) {
        log.info("Activating category: {}", id);

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        category.setActive(true);
        categoryRepository.save(category);

        log.info("Category {} activated", id);
    }

    @Transactional
    public void deactivateCategory(Long id) {
        log.info("Deactivating category: {}", id);

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        category.setActive(false);
        categoryRepository.save(category);

        log.info("Category {} deactivated", id);
    }

    public CategoryEntity findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}
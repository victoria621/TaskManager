package com.example.TaskManager.service;

import com.example.TaskManager.dto.CategoryRequest;
import com.example.TaskManager.dto.CategoryResponse;
import com.example.TaskManager.entity.CategoryEntity;
import com.example.TaskManager.exception.ResourceAlreadyExistsException;
import com.example.TaskManager.exception.ResourceCannotBeDeletedException;
import com.example.TaskManager.exception.ResourceNotFoundException;
import com.example.TaskManager.mapper.CategoryMapper;
import com.example.TaskManager.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        if (categoryRepository.existsByName(requestDTO.categoryName())) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.categoryName());
        }

        CategoryEntity category = categoryMapper.toEntity(requestDTO);
        category.setActive(true);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoryResponse getCategoryById(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        return categoryMapper.toDto(category);
    }

    public CategoryResponse getCategoryByName(String name) {
        CategoryEntity category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, name));
        return categoryMapper.toDto(category);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findAll().stream()
                .filter(CategoryEntity::getActive)
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest requestDTO) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!category.getCategoryName().equals(requestDTO.categoryName()) &&
                categoryRepository.existsByName(requestDTO.categoryName())) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, requestDTO.categoryName());
        }

        category.setCategoryName(requestDTO.categoryName());
        category.setCategoryDescription(requestDTO.categoryDescription());

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        if (!category.getTasks().isEmpty()) {
            throw new ResourceCannotBeDeletedException(RESOURCE_NAME, "category has existing tasks");
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public void activateCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        category.setActive(true);
        categoryRepository.save(category);
    }

    @Transactional
    public void deactivateCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public CategoryEntity findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}
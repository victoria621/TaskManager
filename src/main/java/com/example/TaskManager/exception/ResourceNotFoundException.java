package com.example.TaskManager.exception;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }

    public ResourceNotFoundException(String resource, String value) {
        super(resource + " not found with: " + value);
    }
}
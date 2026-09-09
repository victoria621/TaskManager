package com.example.TaskManager.exception;

public class ResourceAlreadyExistsException extends BusinessException {
    public ResourceAlreadyExistsException(String resource, String value) {
        super(resource + " already exists: " + value);
    }

    public ResourceAlreadyExistsException(String resource, Long id) {
        super(resource + " already exists with id: " + id);
    }
}
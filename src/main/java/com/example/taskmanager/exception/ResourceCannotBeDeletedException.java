package com.example.taskmanager.exception;

public class ResourceCannotBeDeletedException extends BusinessException {
    public ResourceCannotBeDeletedException(String resource, String reason) {
        super("Cannot delete " + resource + ": " + reason);
    }
}
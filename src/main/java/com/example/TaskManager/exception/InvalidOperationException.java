package com.example.TaskManager.exception;

public class InvalidOperationException extends BusinessException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
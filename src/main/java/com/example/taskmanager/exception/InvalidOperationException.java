package com.example.taskmanager.exception;

public class InvalidOperationException extends BusinessException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
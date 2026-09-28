package com.example.taskmanager.strategy;

import com.example.taskmanager.entity.TaskEntity;
import com.example.taskmanager.entity.TaskStatus;

public interface TaskValidationStrategy {
    TaskStatus getTaskStatus();
    void validate(TaskEntity task);
}

package com.example.taskmanager.strategy.impl;

import com.example.taskmanager.entity.TaskEntity;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.InvalidOperationException;
import com.example.taskmanager.strategy.TaskValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CanceledValidationStrategy implements TaskValidationStrategy {

    @Override
    public TaskStatus getTaskStatus(){
        return TaskStatus.CANCELLED;
    }

    @Override
    public void validate(TaskEntity task) {
        log.debug("Validating CANCELLED task {}", task.getTaskId());

        if (task.getStatus() != TaskStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot modify cancelled task");
        }
    }
}

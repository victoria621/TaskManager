package com.example.taskmanager.strategy.impl;

import com.example.taskmanager.entity.TaskEntity;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.InvalidOperationException;
import com.example.taskmanager.strategy.TaskValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DoneValidationStrategy implements TaskValidationStrategy {

    @Override
    public TaskStatus getTaskStatus() {
        return TaskStatus.DONE;
    }

    @Override
    public void validate(TaskEntity task) {
        log.debug("Validating done task {}", task.getTaskId());

        if (task.getCompletedAt() == null) {
            throw new InvalidOperationException("DONE task must have completedAt date");
        }
    }

}

package com.example.taskmanager.strategy.impl;

import com.example.taskmanager.entity.TaskEntity;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.InvalidOperationException;
import com.example.taskmanager.strategy.TaskValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ToDoValidationStrategy implements TaskValidationStrategy {

    @Override
    public TaskStatus getTaskStatus() {
        return TaskStatus.TODO;
    }

    @Override
    public void validate(TaskEntity task){
        log.debug("Validating TODO task {}", task.getTaskId());

        if(task.getTitle() == null || task.getTitle().isEmpty()){
            throw new InvalidOperationException("Task title is empty");
        }
    }

}

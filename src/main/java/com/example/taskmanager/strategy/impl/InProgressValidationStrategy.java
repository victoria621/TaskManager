package com.example.taskmanager.strategy.impl;

import com.example.taskmanager.entity.TaskEntity;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.InvalidOperationException;
import com.example.taskmanager.strategy.TaskValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class InProgressValidationStrategy implements TaskValidationStrategy {

    @Override
    public TaskStatus getTaskStatus(){
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    public void validate(TaskEntity task){
        log.debug("Validating In progress task {}", task.getTaskId());

        if(task.getDueDate() == null || task.getDueDate().isBefore(LocalDateTime.now())){
            throw new InvalidOperationException("Due date is required");
        }
    }
}

package com.example.taskmanager.strategy;

import com.example.taskmanager.entity.TaskEntity;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.InvalidOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskValidationService {

    private final Map<TaskStatus, TaskValidationStrategy> strategies;

    public TaskValidationService(List<TaskValidationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        TaskValidationStrategy::getTaskStatus,
                        Function.identity()
                ));

        log.info("Loaded {} task validation strategies", strategies.size());
    }

    public void validate(TaskEntity task) {
        TaskValidationStrategy strategy = strategies.get(task.getStatus());

        if (strategy == null) {
            throw new InvalidOperationException(
                    "No validation strategy for status: " + task.getStatus()
            );
        }

        log.debug("Using strategy: {}", strategy.getClass().getSimpleName());
        strategy.validate(task);
    }
}
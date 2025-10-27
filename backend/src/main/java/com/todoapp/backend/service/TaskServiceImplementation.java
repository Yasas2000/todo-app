package com.todoapp.backend.service;

import com.todoapp.backend.dto.TaskCreateRequest;
import com.todoapp.backend.dto.TaskResponse;
import com.todoapp.backend.entity.Task;
import com.todoapp.backend.exception.ResourceNotFoundException;
import com.todoapp.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TaskService interface.
 * Contains all business logic for Task operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImplementation implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        log.info("Creating new task with title: {}", request.getTitle());
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(false);
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());
        
        return convertToResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getRecentTasks() {
        log.info("Fetching 5 most recent active tasks");
        
        List<Task> tasks = taskRepository.findTop5ByCompletedFalseOrderByCreatedAtDesc();
        log.info("Found {} active tasks", tasks.size());
        
        return tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse completeTask(Long id) {
        log.info("Marking task as completed: {}", id);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        
        task.setCompleted(true);
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task {} marked as completed", id);
        return convertToResponse(updatedTask);
    }

    /**
     * Converts Task entity to TaskResponse DTO.
     * Keeps entity-to-DTO conversion logic centralized.
     */
    private TaskResponse convertToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
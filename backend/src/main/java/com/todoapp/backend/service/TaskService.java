package com.todoapp.backend.service;

import com.todoapp.backend.dto.TaskCreateRequest;
import com.todoapp.backend.dto.TaskResponse;

import java.util.List;

/**
 * Service interface for Task business logic.
 * Follows Interface Segregation Principle (SOLID).
 */
public interface TaskService {
    
    /**
     * Creates a new task.
     * @param request Task creation request
     * @return Created task response
     */
    TaskResponse createTask(TaskCreateRequest request);
    
    /**
     * Retrieves the 5 most recent non-completed tasks.
     * @return List of up to 5 recent active tasks
     */
    List<TaskResponse> getRecentTasks();

    /**
     * Deletes all tasks - for testing purposes.
     */
    void deleteAllTasks();
    
    /**
     * Marks a task as completed.
     * @param id Task ID
     * @return Updated task response
     */
    TaskResponse completeTask(Long id);
}
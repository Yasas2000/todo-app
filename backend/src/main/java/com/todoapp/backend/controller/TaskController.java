package com.todoapp.backend.controller;

import com.todoapp.backend.dto.TaskCreateRequest;
import com.todoapp.backend.dto.TaskResponse;
import com.todoapp.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Task operations.
 * Handles HTTP requests and delegates business logic to service layer.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173") // Allow React dev server
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new task.
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        log.info("POST /api/tasks - Creating new task");
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Gets the 5 most recent non-completed tasks.
     * GET /api/tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getRecentTasks() {
        log.info("GET /api/tasks - Fetching recent tasks");
        List<TaskResponse> tasks = taskService.getRecentTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Deletes all tasks - FOR TESTING PURPOSES ONLY.
     * DELETE /api/tasks
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllTasks() {
        log.warn("DELETE /api/tasks - Deleting ALL tasks (test endpoint)");
        taskService.deleteAllTasks();
        return ResponseEntity.noContent().build();
    }

    /**
     * Marks a task as completed.
     * PUT /api/tasks/{id}/complete
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id) {
        log.info("PUT /api/tasks/{}/complete - Marking task as completed", id);
        TaskResponse response = taskService.completeTask(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     * GET /api/tasks/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Task API is running");
    }
}
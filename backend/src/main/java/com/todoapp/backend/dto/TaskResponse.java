package com.todoapp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for task responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

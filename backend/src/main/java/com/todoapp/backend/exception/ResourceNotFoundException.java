package com.todoapp.backend.exception;

/**
 * Custom exception thrown when a requested resource is not found.
 * Results in HTTP 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s not found with id: %d", resource, id));
    }
}
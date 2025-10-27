package com.todoapp.backend.repository;

import com.todoapp.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Task entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Retrieves the 5 most recent non-completed tasks ordered by creation date.
     * @return List of up to 5 recent active tasks
     */
    List<Task> findTop5ByCompletedFalseOrderByCreatedAtDesc();
    
}

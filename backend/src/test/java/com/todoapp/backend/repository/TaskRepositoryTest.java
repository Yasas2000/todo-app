package com.todoapp.backend.repository;

import com.todoapp.backend.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository layer tests for Task entity.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveAndRetrieveTask() {
        // Given
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setCompleted(false);

        // When
        Task savedTask = taskRepository.save(task);

        // Then
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");
        assertThat(savedTask.getCreatedAt()).isNotNull();
    }
}
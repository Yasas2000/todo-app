package com.todoapp.backend.service;

import com.todoapp.backend.dto.TaskCreateRequest;
import com.todoapp.backend.dto.TaskResponse;
import com.todoapp.backend.entity.Task;
import com.todoapp.backend.exception.ResourceNotFoundException;
import com.todoapp.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskServiceImpl.
 * Uses Mockito for mocking dependencies - fast execution without database.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceImplementationTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImplementation taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setCompleted(false);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create task successfully")
    void createTask_ShouldReturnCreatedTask() {
        // Given
        TaskCreateRequest request = new TaskCreateRequest("New Task", "New Description");
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.createTask(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getCompleted()).isFalse();
        
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getCompleted()).isFalse();
    }

    @Test
    @DisplayName("Should get recent tasks in descending order")
    void getRecentTasks_ShouldReturnTop5Tasks() {
        // Given
        List<Task> tasks = Arrays.asList(testTask, testTask, testTask);
        when(taskRepository.findTop5ByCompletedFalseOrderByCreatedAtDesc()).thenReturn(tasks);

        // When
        List<TaskResponse> responses = taskService.getRecentTasks();

        // Then
        assertThat(responses).hasSize(3);
        verify(taskRepository, times(1)).findTop5ByCompletedFalseOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void getRecentTasks_WithNoTasks_ShouldReturnEmptyList() {
        // Given
        when(taskRepository.findTop5ByCompletedFalseOrderByCreatedAtDesc())
            .thenReturn(Collections.emptyList());

        // When
        List<TaskResponse> responses = taskService.getRecentTasks();

        // Then
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Should mark task as completed")
    void completeTask_ShouldMarkTaskAsCompleted() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.completeTask(1L);

        // Then
        assertThat(response).isNotNull();
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getCompleted()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when completing non-existent task")
    void completeTask_WithInvalidId_ShouldThrowException() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.completeTask(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 999");
        
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should correctly convert Task to TaskResponse")
    void createTask_ShouldConvertToResponseDTO() {
        // Given
        TaskCreateRequest request = new TaskCreateRequest("Title", "Description");
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.createTask(request);

        // Then
        assertThat(response.getId()).isEqualTo(testTask.getId());
        assertThat(response.getTitle()).isEqualTo(testTask.getTitle());
        assertThat(response.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(response.getCompleted()).isEqualTo(testTask.getCompleted());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }
}
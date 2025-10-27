package com.todoapp.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.backend.dto.TaskCreateRequest;
import com.todoapp.backend.dto.TaskResponse;
import com.todoapp.backend.entity.Task;
import com.todoapp.backend.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for TaskController using TestContainers.
 * Tests the full stack from controller to database with a real PostgreSQL container.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = 
        new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should create a new task successfully")
    void shouldCreateTask() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest(
            "Integration Test Task",
            "Testing with real PostgreSQL database"
        );

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.description").value("Testing with real PostgreSQL database"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @Order(2)
    @DisplayName("Should return validation error for empty title")
    void shouldReturnValidationErrorForEmptyTitle() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest("", "Description");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    @Order(3)
    @DisplayName("Should retrieve recent tasks")
    void shouldGetRecentTasks() throws Exception {
        // Create 3 tasks
        createTaskInDB("Task 1", "Description 1");
        createTaskInDB("Task 2", "Description 2");
        createTaskInDB("Task 3", "Description 3");

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title").value("Task 3")) // Most recent first
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[2].title").value("Task 1"));
    }

    @Test
    @Order(4)
    @DisplayName("Should return only 5 most recent tasks when more exist")
    void shouldReturnOnly5RecentTasks() throws Exception {
        // Create 7 tasks
        for (int i = 1; i <= 7; i++) {
            createTaskInDB("Task " + i, "Description " + i);
            Thread.sleep(10); // Small delay to ensure different timestamps
        }

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].title").value("Task 7")); // Most recent
    }

    @Test
    @Order(5)
    @DisplayName("Should mark task as completed")
    void shouldCompleteTask() throws Exception {
        Task task = createTaskInDB("Task to Complete", "Will be completed");

        mockMvc.perform(put("/api/tasks/" + task.getId() + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @Order(6)
    @DisplayName("Should return 404 when completing non-existent task")
    void shouldReturn404WhenCompletingNonExistentTask() throws Exception {
        mockMvc.perform(put("/api/tasks/999/complete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
    }

    @Test
    @Order(7)
    @DisplayName("Should not return completed tasks in recent list")
    void shouldNotReturnCompletedTasks() throws Exception {
        Task task1 = createTaskInDB("Active Task", "Still active");
        Task task2 = createTaskInDB("Completed Task", "Will be completed");
        
        // Complete task2
        task2.setCompleted(true);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(task1.getId()));
    }

    @Test
    @Order(8)
    @DisplayName("Should validate title length")
    void shouldValidateTitleLength() throws Exception {
        String longTitle = "a".repeat(101); // Exceeds 100 character limit
        TaskCreateRequest request = new TaskCreateRequest(longTitle, "Description");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("Health check should return OK")
    void healthCheckShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/tasks/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task API is running"));
    }

    /**
     * Helper method to create a task in the database
     */
    private Task createTaskInDB(String title, String description) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(false);
        return taskRepository.save(task);
    }
}

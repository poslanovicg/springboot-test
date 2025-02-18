package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/*
 * This test class checks the "read" functionality
 */
public class TaskControllerTestRead extends TaskControllerTestBase {
    // Creates a mock version of TaskRepository 
    // This allows us to control its behavior during tests without accessing a real DB
    @Mock
    private TaskRepository taskRepository;

    // Creates an instance of TaskController and inject the mock TaskRepo into it
    // This means the controller will use the simulated repository instead of a real one
    @InjectMocks
    private TaskController taskController;

    // This method runs before each test (BeforeEach Annotation)
    @BeforeEach
    public void setUp() {
        // Initialize all fields
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test: Successfully return a list of all tasks
     */
    @Test
    public void testGetAllTasks() {
        System.out.println("----------Starting testGetAllTasks----------");

        // Create a list of tasks to simulate repository data
        List<Task> tasks = new ArrayList<>();
        tasks.add(createTestTask("Title 1", "Description 1"));
        tasks.add(createTestTask("Title 2", "Description 2"));
        System.out.println("Created tasks list: " + tasks);

        // Simulate to return our simulated list when findAll() is called.
        when(taskRepository.findAll()).thenReturn(tasks);

        // Call the controller's getAllTasks method
        List<Task> result = taskController.getAllTasks();
        System.out.println("Result from getAllTasks(): " + result);

        // Verify that the returned list has the expected size (2 tasks).
        assertEquals(2, result.size(), "Expected two tasks in the result list");

        // Verify that the returned list exactly matches our simulated list.
        assertEquals(tasks, result, "Expected tasks list to match the simulated list");

        System.out.println("testGetAllTasks completed successfully");
    }

    /**
     * Test: Return a Task when it exists
     */
    @Test
    public void testGetTaskById_Found() {
        System.out.println("----------Starting testGetTaskById_Found----------");

        // Create a Task object and assign it an ID.
        Task task = createTestTask("Title Found", "Description Found");
        task.setId(1L);
        System.out.println("Created task: " + task);

        // When findById(1L) is called, return an Optional containing our task.
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Call the controller's getTaskById method
        ResponseEntity<?> response = taskController.getTaskById(1L);
        System.out.println("Response received: " + response);

        // Check that the response has a status of 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected HTTP 200 OK");

        // The response body should be the Task object
        Task returnedTask = (Task) response.getBody();

        // Verify that the returned Task matches the simulated one
        assertEquals(task, returnedTask, "Expected the returned task to match the simulated task");
        System.out.println("testGetTaskById_Found completed successfully");
    }

    /**
     * Test: Trying to find task which does not exist
     */
    @Test
    public void testGetTaskById_NotFound() {
        System.out.println("----------Starting testGetTaskById_NotFound----------");

        // When findById(1L) is called, return an empty Optional
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the controller's getTaskById method with ID 1
        ResponseEntity<?> response = taskController.getTaskById(1L);
        System.out.println("Response received: " + response);

        // Verify that the response status is 404 NOT_FOUND.
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Expected HTTP 404 NOT_FOUND");

        // The response body should be a Map containing an error message
        Map<String, String> errors = extractErrors(response);

        // Verify that the error map contains the key "error".
        assertTrue(errors.containsKey("error"), "Expected error key in response body but got: " + errors);
        System.out.println("testGetTaskById_NotFound completed successfully");
    }
}

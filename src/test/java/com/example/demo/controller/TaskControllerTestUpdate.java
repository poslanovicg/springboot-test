package com.example.demo.controller;

import com.example.demo.controller.TaskControllerTestBase;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*
 * This test class checks the "update" functionality
 */
public class TaskControllerTestUpdate extends TaskControllerTestBase {

    // Creates a mock version of TaskRepository 
    // This allows us to control its behavior during tests without accessing a real DB
    @Mock
    private TaskRepository taskRepository;

    // Creates an instance of TaskController and inject the mock TaskRepo into it
    // This means the controller will use the simulated repository instead of a real one
    @InjectMocks
    private TaskController taskController;

    // This method runs befor each test (BeforeEach Annotation)
    @BeforeEach
    public void setUp() {
        // Initialize all fields
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test: Successfully update a task when valid input is provided
     */
    @Test
    public void testUpdateTask_success() {
        System.out.println("----------Starting testUpdateTask_success----------");

        // Create an existing task with a valid title and description
        Task task = createTestTask("Old Title", "Old Description");
        task.setId(1L);

        // Simulate returning an Optional containing our task
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Create a map containing the updates
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "New Title");
        updates.put("description", "New Description");
        updates.put("completed", true);

        // Simulate that no task with the same title exists 
        when(taskRepository.existsByTitle("New Title")).thenReturn(false);

        // Simulate that saving the task returns the updated task object
        Task updatedTask = createTestTask("New Title", "New Description");
        updatedTask.setId(1L);
        updatedTask.setCompleted(true);
        when(taskRepository.save(task)).thenReturn(updatedTask);

        // Call the conrollers updateTask method
        ResponseEntity<?> response = taskController.updateTask(1L, updates);
        System.out.println("Response received: " + response);

        // Check that the response has a status of 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check that the fields match the updates
        Task returnedTask = (Task) response.getBody();
        assertEquals("New Title", returnedTask.getTitle(), "Title was not updated correctly");
        assertEquals("New Description", returnedTask.getDescription(), "Description was not updated correctly");
        assertTrue(returnedTask.isCompleted(), "Completed status was not updated correctly");

        System.out.println("testUpdateTask_success completed successfully");
    }

    /**
     * Test: Attempting to update a task that does not exist
     */
    @Test
    public void testUpdateTask_notFound() {
        System.out.println("----------Starting testUpdateTask_notFound----------");

        // Simulate that no task exists for ID 1
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Create an update map with some updates
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "New Title");

        // Call the controllers updateTask method with ID 1
        ResponseEntity<?> response = taskController.updateTask(1L, updates);
        System.out.println("Response received: " + response);

        // Verify that the response status is 404 NOT_FOUND.
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        System.out.println("testUpdateTask_notFound completed successfully");
    }

    /**
     * Test: Updating a task with an empty title
     */
    @Test
    public void testUpdateTask_invalidTitle() {
        System.out.println("----------Starting testUpdateTask_invalidTitle----------");

        // Create a new Task with a valid title and description
        Task task = createTestTask("Old Title", "Old Description");
        task.setId(1L);

        // Simulate returning the task
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Create an update map with an empty title
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", ""); 

        // Call the controllers updateTask method
        ResponseEntity<?> response = taskController.updateTask(1L, updates);
        System.out.println("Response received: " + response);

        // Verify that the response status is 400 BAD REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Verify that the Map contains a key "title"
        Map<String, String> errors = extractErrors(response);
        assertTrue(errors.containsKey("title"), "Expected error for key 'title', but got: " + errors);

        System.out.println("testUpdateTask_invalidTitle completed successfully");
    }

    /**
     * Test: Updating a task with an invalid "completed" value
     */
    @Test
    public void testUpdateTask_invalidCompleted() {
        System.out.println("----------Starting testUpdateTask_invalidCompleted----------");

        // Create a new Task with a valid title and description
        Task task = createTestTask("Old Title", "Old Description");
        task.setId(1L);

        // Simulate returning the task.
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Create an update map with an invalid value for "completed"
        Map<String, Object> updates = new HashMap<>();
        updates.put("completed", "test");

        // Call the controllers updateTask method
        ResponseEntity<?> response = taskController.updateTask(1L, updates);
        System.out.println("Response received: " + response);

        // Verify that the response status is 400 BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Verify that the Map contains a key "completed"
        Map<String, String> errors = extractErrors(response);
        assertTrue(errors.containsKey("completed"), "Expected error for key 'completed', but got: " + errors);

        System.out.println("testUpdateTask_invalidCompleted completed successfully");
    }
}

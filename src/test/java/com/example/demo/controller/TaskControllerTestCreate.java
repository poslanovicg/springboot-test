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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * This test class checks the "create" functionality
 */
public class TaskControllerTestCreate extends TaskControllerTestBase {
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
     * Test: Successfully create a task when valid input is provided
     */
    @Test
    public void testCreateTask_success() {
        System.out.println("----------Starting testCreateTask_Success----------");

        // Create a new Task with a valid title and description
        Task task = createTestTask("Test Title", "Test Description");

        // Simulate that no task with the same title exists 
        when(taskRepository.existsByTitle(task.getTitle())).thenReturn(false);
        System.out.println("existsByTitle: returning false for title '" + task.getTitle() + "'");

        // Simulate that saving the task returns the same task object
        when(taskRepository.save(task)).thenReturn(task);
        System.out.println("Save: returning the task");

        // call the controllers createTask method
        ResponseEntity<?> response = taskController.createTask(task);
        System.out.println("Response received: " + response);

        // Check that the response has a status of 201 CREATED
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Check that the response body (the saved task) is the same as our task
        assertEquals(task, response.getBody());
        System.out.println("testCreateTask_Success completed successfully");
    }

    /**
    * Test: Creating a Task with a missing title
    */
    @Test
    public void testCreateTask_missingTitle() {
        System.out.println("----------Starting testCreateTask_MissingTitle----------");

        // Create a Task with an empty title and a valid description
        Task task = createTestTask("", "Test Description");

        // Call the controller's createTask method
        ResponseEntity<?> response = taskController.createTask(task);
        System.out.println("Response received: " + response);

        // Check that the response status is 400 BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // The response body should contain a map of error messages
        Map<String, String> errors = extractErrors(response);

        // Check that the errors map has an error for the "title" key
        assertTrue(errors.containsKey("title"), "Expected errors to contain key 'title', but got: " + errors);
        System.out.println("testCreateTask_MissingTitle completed successfully");
    }

    /**
     * Test: Creating a Task with a missing description
     */
    @Test
    public void testCreateTask_missingDescription() {
        System.out.println("----------Starting testCreateTask_missingDescription----------");

        // Create a Task with an empty description and a valid title.
        Task task = createTestTask("Test Title", "");
        
        // Call the controller's createTask method
        ResponseEntity<?> response = taskController.createTask(task);
        System.out.println("Response received: " + response);
        
        // Check that the response status is 400 BAD_REQUEST.
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        // The response body should contain a map of error messages.
        Map<String, String> errors = extractErrors(response);
        
        // Check that the errors map has an error for the "description" key.
        assertTrue(errors.containsKey("description"), "Expected errors to contain key 'description', but got: " + errors);
        System.out.println("testCreateTask_missingDescription completed successfully");
    }


    /*
     * Test: Creating a Task when a task with the same title already exists
     */
    @Test
    public void testCreateTask_titleExists(){
        System.out.println("----------Starting testCreateTask_titleExists----------");

        // Create a Task with a valid title and description
        Task task = createTestTask("Test Title", "Test Description");

        // Simulate that a task with the same title already exists by returning true.
        when(taskRepository.existsByTitle(task.getTitle())).thenReturn(true);
        System.out.println("existsByTitle: returning true for title '" + task.getTitle() + "'");

        // Call the controller's createTask method
        ResponseEntity<?> response = taskController.createTask(task);
        System.out.println("Response received: " + response);

        // Check that the response status is 400 BAD_REQUEST.
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // The response body should contain a map of error messages.
        Map<String, String> errors = extractErrors(response);

        // Verify that the error message in the map is what we expect.
        assertEquals("Task Title already exists.", errors.get("error"),
            "Expected error message 'Task Title already exists.' but got: " + errors.get("error"));
        System.out.println("testCreateTask_titleExists completed successfully");
    }

    /*
     * Test: Creating a task with both a missing title and description
     */
    @Test 
    public void testCreateTask_missingTitleAndDescription(){
        System.out.println("----------Starting testCreateTask_missingTitleAndDescription----------");

        // Create a Task with missing title and description
        Task task = createTestTask("", "");

        // Call the controller's createTask method.
        ResponseEntity<?> response = taskController.createTask(task);
        System.out.println("Response received: " + response);

        // Verify that the response status is 400 BAD_REQUEST.
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // The response body should contain a map of error messages.
        Map<String, String> errors = extractErrors(response);

        // Verify that the errors map contains errors for both "title" and "description".
        assertTrue(errors.containsKey("title"), "Expected errors to contain key 'title', but got: " + errors);
        assertTrue(errors.containsKey("description"), "Expected errors to contain key 'description', but got: " + errors);
        System.out.println("testCreateTask_missingTitleAndDescription completed successfully");
    }
}

package com.example.demo.controller;

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

import java.util.Map;
import java.util.Optional;

/*
 * This test class checks the "delete" functionality
 */
public class TaskControllerTestDelete {
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

  /*
   * Test: Successfully delete a task when the task exists
   */
  @Test 
  public void testDeleteTaskById_success(){
    System.out.println("----------Starting testDeleteTask_success----------");

    // Create a new Task with a valid title and description
    Task task = new Task("Task 1", "Task 1 Description");
    task.setId(1L);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    // Call the controller's deleteTask method with ID 1.
    ResponseEntity<?> response = taskController.deleteTask(1L);
    System.out.println("Response received: " + response);

    // Check that the response status is 200 OK
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // The response body should be a Map containing a success message.
    Map<String, String> result = (Map<String, String>) response.getBody();
    System.out.println("Response body: " + result);

    // Verify that the Map contains a key "success".
    assertTrue(result.containsKey("success"), "Expected errors to contain key 'success', but got: " + result);

    // Verify that the success message is exactly what we expect.
    assertEquals("Deleted Task with ID: 1", result.get("success"),
        "Expected success message 'Deleted Task with ID: 1' but got: " + result.get("success"));
    
    // Verify that the repository's deleteById method was called with ID 1.
    verify(taskRepository).deleteById(1L);
    System.out.println("testDeleteTask_success completed successfully");
  }

  /*
   * Test: Attempting to delete a task that does not exist
   */
  @Test 
  public void testDeleteTaskById_notFound(){
    System.out.println("----------Starting testDeleteTaskById_notFound----------");

    // Create a new Task with a valid title and description
    Task task = new Task("Task 1", "Task 1 Description");
    task.setId(1L);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    // Call the controller's deleteTask method with ID 2
    ResponseEntity<?> response = taskController.deleteTask(2L);
    System.out.println("Response received: " + response);

    // Verify that the response status is 404 NOT_FOUND.
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    // The response body should be a Map containing an error message.
    Map<String, String> result = (Map<String, String>) response.getBody();
    System.out.println("Response body: " + result);

    // Verify that the Map contains a key "success".
    assertTrue(result.containsKey("error"), "Expected errors to contain key 'error', but got: " + result);

    // Verify that the success message is exactly what we expect.
    String expectedError = "Task with ID: 2 couldn't be deleted. Task doesn't exist.";
    assertEquals(expectedError, result.get("error"),
        "Expected error message 'Couldn't find Task with ID: 2' but got: " + result.get("success"));
    System.out.println("testDeleteTaskById_notFound completed successfully");
  }
}

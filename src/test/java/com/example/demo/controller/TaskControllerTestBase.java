package com.example.demo.controller;

import com.example.demo.model.Task;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/*
 * Abstract base class for TaskController test classes
 */
public abstract class TaskControllerTestBase {

    /**
     * Helper method to create a Task object
     * 
     * @param title       The title for the task
     * @param description The description for the task
     * @return A new Task object
     */
    protected Task createTestTask(String title, String description) {
        Task task = new Task(title, description);
        System.out.println("Created task: " + task);
        return task;
    }

    /**
     * Helper method to extract result messages from a ResponseEntity.
     * 
     * @param response The ResponseEntity returned by the controller.
     * @return A Map of result messages.
     */
    protected Map<String, String> extractErrors(ResponseEntity<?> response) {
        Map<String, String> errors = (Map<String, String>) response.getBody();
        System.out.println("Result map: " + errors);
        return errors;
    }
}

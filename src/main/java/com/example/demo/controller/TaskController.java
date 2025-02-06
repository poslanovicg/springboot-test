package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        /**
         * Explanation for myself: 
         *      https://docs.oracle.com/javase/8/docs/api/java/util/Map.html
         *      https://docs.oracle.com/javase/tutorial/collections/interfaces/map.html
         * A Map<K, V> is a data structure that stores key-value pairs that maps keys to values
         * Map<String, String> means both the key and value are Strings
         * The Map interface includes operations like put(), get(), remove(), etc.
         * 
         * Types of Map Implementations in Java 
         * - HashMap: Fastest but unordered
         * - LinkedHashMap: Maintains insertion order
         * - TreeMap: Keeps keys sorted
         */
        Map<String, String> errors = new HashMap<>();

        if(task.getTitle() == null || task.getTitle().trim().isEmpty()){
            errors.put("title", "Title is required");
        }

        if(task.getDescription() == null || task.getDescription().trim().isEmpty()){
            errors.put("description", "Description is required");
        }

        if(!errors.isEmpty()){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
        }

        if(taskRepository.existsByTitle(task.getTitle())){
            return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", "Task Title already exists."));
        }

        Task savedTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }
}

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
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Helper Method to find a specific task by its ID 
    private Optional<Task> findTask(Long id){
        return taskRepository.findById(id);
    }

    // Helper method to return a 404 Not Found error response
    private ResponseEntity<?> notFoundError(String text){
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", text));
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        /**
         * Documentation for myself: 
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

    @GetMapping
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) { 
        /*
         * Documentation for myself: 
         * Optional<T> is a container object which may or may not contain a non-null value.
         * If a value is present, isPresent() will return true and get() will return the value
         * 
         * .isPresent() & get() are both methods from Java's Optional class 
         * 
         * .isPresent() checks if a value exists inside the Optional
         * .get() retrieves the value inside the Optional
         */
        Optional<Task> task = findTask(id);
        
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } 
        
        return notFoundError("Task with ID: " + id + " couldn't be found. Task doesn't exist.");
    }    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        Optional<Task> task = findTask(id);
    
        if (task.isPresent()) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Deleted Task with ID: " + id));
        } 
    
        return notFoundError("Task with ID: " + id + " couldn't be deleted. Task doesn't exist.");
    }
}

package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.http.HttpStatus;
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

    /*
     * Validates the given field value for a new Task object
     * 
     * @param value         The field value to validate 
     * @param fieldName     The name of the field which is used as the key in the errors map
     * @param errorMessage  The error message to add if the validation fails 
     * @param errors        The map where the error message(s) will be stored
     */
    private void validateFields(String value, String fieldName, String errorMessage, Map<String, String> errors){
        if(value == null || value.trim().isEmpty()){
            errors.put(fieldName, errorMessage);
        }
    }

    /*
     * FInds a specific task by its ID 
     * 
     * @param id            The id of the Task to be found
     * @return an Optional containing the Task if found or an empty Optional if not
     */
    private Optional<Task> findTask(Long id){
        return taskRepository.findById(id);
    }

    /*
     * Returns a ResponseEntity representing a 404 Not Found error with a custom error message
     * 
     * @param text          The error message to be included in the response body
     * @return a ResponseEntity with a 404 staus and a body containing the error message
     */
    private ResponseEntity<?> notFoundError(String text){
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", text));
    }

    /*
     * Creates a new Task object
     * 
     * @param task          The Task object to create
     * @return a ResponseEntity containing the created Task or an error message if validation fails
     */
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

        validateFields(task.getTitle(), "title", "Title is required", errors);
        validateFields(task.getDescription(), "description", "Description is required", errors);

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

    /*
     * Retrieves all Tasks
     * 
     * @return a List of all Task objects
     */
    @GetMapping
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    /*
     * Retrieves a specific task by its ID
     * 
     * @param id            The id of the Task to retrieve
     * @return a ResponseEntity containing the Task if found or an error message if not 
     */
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

    /*
     * Updates an existing Task
     * 
     * @param id            The id of the Task to updated
     * @param updates       A Map containing the fields to update and their new values 
     * @return a ResponseEntity with the updated Task if the update is successful or an error message if not 
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> updates){
        Optional<Task> task = findTask(id);
        Map<String, String> errors = new HashMap<>();

        if(task.isPresent()){
            Task updateTask = task.get();

            if(updates.containsKey("title")){
                String title = updates.get("title").toString().trim();
                if(title.isEmpty()){
                    errors.put("title", "Title cannot be empty");
                } else if(!title.equals(updateTask.getTitle()) && taskRepository.existsByTitle(title)){
                    errors.put("title", "Title with name: " + title + " already exists.");
                } else {
                    updateTask.setTitle(title);
                }
            }

            if(updates.containsKey("description")){
                String description = updates.get("description").toString().trim();
                if(description.isEmpty()){
                    errors.put("description", "Description cannot be empty");
                } else {
                    updateTask.setDescription(description);
                }
            }

            if(updates.containsKey("completed")){
                if(updates.get("completed") instanceof Boolean) {
                    updateTask.setCompleted((Boolean) updates.get("completed"));
                } else {
                    errors.put("completed", "Completed must be a boolean value.");
                }
            }

            if(!errors.isEmpty()){
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errors);
            }

            Task updatedTask = taskRepository.save(updateTask);
            return ResponseEntity.ok(updatedTask);
        }
        return notFoundError("Task with ID: " + id + " couldn't be updated. Task doesn't exist.");
    }

    /*
     * Deleted a specific task by its ID 
     * 
     * @param id            The id of the task to delete 
     * @return a ResponseEntity with a success message if deletion is successful or an error message if not
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        Optional<Task> task = findTask(id);
    
        if (task.isPresent()) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", "Deleted Task with ID: " + id));
        } 
        
        return notFoundError("Task with ID: " + id + " couldn't be deleted. Task doesn't exist.");
    }
}

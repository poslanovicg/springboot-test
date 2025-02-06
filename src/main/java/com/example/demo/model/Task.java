package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tasks")
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Title is required") 
  private String title;

  @NotBlank(message = "Description is required")
  private String description;

  private boolean completed = false;

  // Constructor
  public Task() {}

  public Task(String title, String description) {
    this.title = title;
    this.description = description;
  }

  // Getter & Setter Methods
  public Long getId() { return id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public boolean isCompleted() { return completed; }
  public void setCompleted(boolean completed) { this.completed = completed; }
}
package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks", uniqueConstraints = {@UniqueConstraint(columnNames = "title")})
@Getter
@Setter
@NoArgsConstructor
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Title is required") 
  @Column(unique = true)
  private String title;

  @NotBlank(message = "Description is required")
  private String description;

  private boolean completed = false;

  public Task(String title, String description) {
    this.title = title;
    this.description = description;
  }
}
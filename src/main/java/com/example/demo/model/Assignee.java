
package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "assignees")
@Getter
@Setter
@NoArgsConstructor
public class Assignee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @ManyToMany(mappedBy = "assignees")
    private Set<Task> tasks;

    public Assignee(String name) {
        this.name = name;
    }
}

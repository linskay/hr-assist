package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ElementCollection
    @CollectionTable(name = "vacancy_competency_weights", joinColumns = @JoinColumn(name = "vacancy_id"))
    @MapKeyColumn(name = "competency_name")
    @Column(name = "weight")
    private Map<String, Double> requiredCompetencies = new HashMap<>();
}

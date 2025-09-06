package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Вакансия с требованиями и компетенциями
 */
@Data
@Entity
@Table(name = "vacancies")
public class Vacancy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String grade;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "requirements_json", columnDefinition = "TEXT")
    private String requirementsJson;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Legacy field for backward compatibility
    @ElementCollection
    @CollectionTable(name = "vacancy_competency_weights", joinColumns = @JoinColumn(name = "vacancy_id"))
    @MapKeyColumn(name = "competency_name")
    @Column(name = "weight")
    private Map<String, Double> requiredCompetencies = new HashMap<>();
}

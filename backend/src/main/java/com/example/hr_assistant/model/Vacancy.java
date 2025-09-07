package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @Column(name = "salary_min")
    private Integer salaryMin;
    
    @Column(name = "salary_max")
    private Integer salaryMax;
    
    @Column
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;
    
    @Enumerated(EnumType.STRING)
    @Column
    private VacancyStatus status = VacancyStatus.ACTIVE;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Legacy field for backward compatibility
    @ElementCollection
    @CollectionTable(name = "vacancy_competency_weights", joinColumns = @JoinColumn(name = "vacancy_id"))
    @MapKeyColumn(name = "competency_name")
    @Column(name = "weight")
    private Map<String, Double> requiredCompetencies = new HashMap<>();
    
    public enum EmploymentType {
        FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP
    }
    
    public enum ExperienceLevel {
        JUNIOR, MIDDLE, SENIOR, LEAD
    }
    
    public enum VacancyStatus {
        ACTIVE, INACTIVE, CLOSED
    }
}

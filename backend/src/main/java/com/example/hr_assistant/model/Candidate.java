package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Кандидат (legacy модель для обратной совместимости)
 * В новой архитектуре данные кандидата хранятся в Interview
 */
@Data
@Entity
@Table(name = "candidates")
public class Candidate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String phone;

    @Lob
    @Column(name = "resume_text")
    private String resumeText;

    @Column(name = "extracted_experience_years")
    private Integer extractedExperienceYears;

    @ElementCollection
    @CollectionTable(name = "candidate_skills", joinColumns = @JoinColumn(name = "candidate_id"))
    @Column(name = "skill")
    private List<String> extractedSkills = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

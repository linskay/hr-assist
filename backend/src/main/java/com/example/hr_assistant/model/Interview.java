package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Интервью с кандидатом
 */
@Data
@Entity
@Table(name = "interviews")
public class Interview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;
    
    @Column(name = "candidate_name", nullable = false)
    private String candidateName;
    
    @Column(name = "candidate_email")
    private String candidateEmail;
    
    @Column(name = "candidate_phone")
    private String candidatePhone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.CREATED;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
    
    @Column(name = "fraud_score")
    private Double fraudScore;
    
    @Column(name = "matching_score")
    private Double matchingScore;
    
    @Column(name = "consent_given")
    private Boolean consentGiven = false;
    
    @Column(name = "consent_timestamp")
    private LocalDateTime consentTimestamp;
    
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();
    
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recording> recordings = new ArrayList<>();
    
    @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Analysis analysis;
    
    @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Antifraud antifraud;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum InterviewStatus {
        CREATED, STARTED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
    }
}

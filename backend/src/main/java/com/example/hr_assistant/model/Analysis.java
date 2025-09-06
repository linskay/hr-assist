package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Анализ интервью и соответствия требованиям
 */
@Data
@Entity
@Table(name = "analysis")
public class Analysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;
    
    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;
    
    @Column(name = "overall_score")
    private Double overallScore;
    
    @Column(name = "competency_scores_json", columnDefinition = "TEXT")
    private String competencyScoresJson;
    
    @Column(name = "strengths_json", columnDefinition = "TEXT")
    private String strengthsJson;
    
    @Column(name = "weaknesses_json", columnDefinition = "TEXT")
    private String weaknessesJson;
    
    @Column(name = "recommendation")
    private String recommendation;
    
    @Column(name = "recommendation_reason", columnDefinition = "TEXT")
    private String recommendationReason;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

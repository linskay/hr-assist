package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Антифрод проверки и флаги
 */
@Data
@Entity
@Table(name = "antifraud")
public class Antifraud {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;
    
    @Column(name = "flags_json", columnDefinition = "TEXT")
    private String flagsJson;
    
    @Column(name = "liveness_score")
    private Double livenessScore;
    
    @Column(name = "face_match_score")
    private Double faceMatchScore;
    
    @Column(name = "voice_match_score")
    private Double voiceMatchScore;
    
    @Column(name = "text_ai_score")
    private Double textAiScore;
    
    @Column(name = "visibility_events_count")
    private Integer visibilityEventsCount = 0;
    
    @Column(name = "heartbeat_gaps")
    private Integer heartbeatGaps = 0;
    
    @Column(name = "devtools_detected")
    private Boolean devtoolsDetected = false;
    
    @Column(name = "tab_switches_count")
    private Integer tabSwitchesCount = 0;
    
    @Column(name = "window_blur_count")
    private Integer windowBlurCount = 0;
    
    @Column(name = "overall_fraud_score")
    private Double overallFraudScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "fraud_status")
    private FraudStatus fraudStatus = FraudStatus.CLEAN;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public enum FraudStatus {
        CLEAN, SUSPICIOUS, FRAUD_DETECTED, MANUAL_REVIEW
    }
}

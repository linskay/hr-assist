package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Транскрипция записи
 */
@Data
@Entity
@Table(name = "transcripts")
public class Transcript {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recording_id", nullable = false)
    private Recording recording;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    
    @Column(name = "words_json", columnDefinition = "TEXT")
    private String wordsJson;
    
    @Column(name = "asr_confidence")
    private Double asrConfidence;
    
    @Column(name = "language")
    private String language = "ru";
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

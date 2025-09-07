package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Запись аудио/видео ответа на вопрос
 */
@Data
@Entity
@Table(name = "recordings")
public class Recording {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordingType type;
    
    @Column(name = "file_url", nullable = false)
    private String fileUrl;
    
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    
    @Column(name = "duration_seconds")
    private Double durationSeconds;
    
    @Column(name = "asr_confidence")
    private Double asrConfidence;
    
    @Column(name = "chunk_index")
    private Integer chunkIndex;
    
    @Column(name = "is_final_chunk")
    private Boolean isFinalChunk = false;
    
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public enum RecordingType {
        AUDIO, VIDEO, AUDIO_VIDEO
    }
}

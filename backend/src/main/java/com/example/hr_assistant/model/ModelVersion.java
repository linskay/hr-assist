package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Версии ML моделей
 */
@Data
@Entity
@Table(name = "model_versions")
public class ModelVersion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "model_name", nullable = false)
    private String modelName;
    
    @Column(name = "version", nullable = false)
    private String version;
    
    @Column(name = "model_path", nullable = false)
    private String modelPath;
    
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;
    
    @Column(name = "is_active")
    private Boolean isActive = false;
    
    @Column(name = "loaded_at")
    private LocalDateTime loadedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public enum ModelType {
        WHISPER_ASR,
        EMBEDDINGS,
        CLASSIFIER,
        VOICE_VERIFICATION,
        FACE_RECOGNITION,
        LIVENESS_DETECTOR,
        AI_TEXT_DETECTOR
    }
}

package com.example.hr_assistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация ML моделей
 */
@Configuration
@ConfigurationProperties(prefix = "ml.models")
@Data
public class MlModelsConfig {

    private String path;
    private String whisper;
    private String embeddings;
    private String classifier;
    private String voiceVerification;
    private String faceRecognition;
    private String liveness;
    private String aiDetector;
}

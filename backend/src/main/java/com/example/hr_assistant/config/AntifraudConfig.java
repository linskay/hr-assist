package com.example.hr_assistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация антифрод системы
 */
@Configuration
@ConfigurationProperties(prefix = "antifraud")
@Data
public class AntifraudConfig {

    private Weights weights = new Weights();
    private Thresholds thresholds = new Thresholds();

    @Data
    public static class Weights {
        private Double liveness = 0.25;
        private Double faceMatch = 0.2;
        private Double voiceMatch = 0.2;
        private Double textAi = 0.2;
        private Double visibility = 0.1;
        private Double devtools = 0.05;
    }

    @Data
    public static class Thresholds {
        private Double reject = 0.85;
        private Double review = 0.6;
    }
}

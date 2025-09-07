package com.example.hr_assistant.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

public class NlpDtos {

    @Data
    public static class SimilarityRequest {
        @NotBlank
        private String text;
        @NotNull
        private List<String> references;
    }

    @Data
    public static class SimilarityResponse {
        private double maxSimilarity;
        private List<Double> similarities;
    }

    @Data
    public static class ClassificationRequest {
        @NotBlank
        private String answer;
        @NotNull
        private Map<String, String> requirements;
        private Map<String, Double> weights;
    }

    @Data
    public static class ClassificationResponse {
        private Map<String, Double> scores;
        private double overallScore;
        private String level; // junior/middle/senior
        private boolean eligible; // годен/не годен
    }
}



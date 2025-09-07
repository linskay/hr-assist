package com.example.hr_assistant.model.dto;

import com.example.hr_assistant.model.Analysis;
import com.example.hr_assistant.model.Antifraud;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Полные результаты интервью
 */
@Data
public class InterviewResultsResponse {
    
    private Long interviewId;
    private String candidateName;
    private String vacancyTitle;
    private LocalDateTime completedAt;
    
    // Анализ соответствия
    private Double matchingScore;
    private Map<String, Double> competencyScores;
    private String recommendation;
    private String recommendationReason;
    
    // Антифрод результаты
    private Double fraudScore;
    private Antifraud.FraudStatus fraudStatus;
    private Double livenessScore;
    private Double faceMatchScore;
    private Double voiceMatchScore;
    private Double textAiScore;
    private Integer visibilityEventsCount;
    private Integer heartbeatGaps;
    private Boolean devtoolsDetected;
    
    // Детали
    private String analysisDetails;
    private String antifraudDetails;
    
    public InterviewResultsResponse(Analysis analysis, Antifraud antifraud) {
        this.interviewId = analysis.getInterview().getId();
        this.candidateName = analysis.getInterview().getCandidateName();
        this.vacancyTitle = analysis.getInterview().getVacancy().getTitle();
        this.completedAt = analysis.getCreatedAt();
        
        this.matchingScore = analysis.getOverallScore();
        this.recommendation = analysis.getRecommendation();
        this.recommendationReason = analysis.getRecommendationReason();
        this.analysisDetails = analysis.getDetailsJson();
        
        if (antifraud != null) {
            this.fraudScore = antifraud.getOverallFraudScore();
            this.fraudStatus = antifraud.getFraudStatus();
            this.livenessScore = antifraud.getLivenessScore();
            this.faceMatchScore = antifraud.getFaceMatchScore();
            this.voiceMatchScore = antifraud.getVoiceMatchScore();
            this.textAiScore = antifraud.getTextAiScore();
            this.visibilityEventsCount = antifraud.getVisibilityEventsCount();
            this.heartbeatGaps = antifraud.getHeartbeatGaps();
            this.devtoolsDetected = antifraud.getDevtoolsDetected();
            this.antifraudDetails = antifraud.getFlagsJson();
        }
    }
}

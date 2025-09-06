package com.example.hr_assistant.model.dto;

import com.example.hr_assistant.model.Interview;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Ответ с информацией об интервью
 */
@Data
public class InterviewResponse {
    
    private Long id;
    private Long vacancyId;
    private String vacancyTitle;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private Interview.InterviewStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Double fraudScore;
    private Double matchingScore;
    private Boolean consentGiven;
    private LocalDateTime consentTimestamp;
    private LocalDateTime createdAt;
    
    public InterviewResponse(Interview interview) {
        this.id = interview.getId();
        this.vacancyId = interview.getVacancy().getId();
        this.vacancyTitle = interview.getVacancy().getTitle();
        this.candidateName = interview.getCandidateName();
        this.candidateEmail = interview.getCandidateEmail();
        this.candidatePhone = interview.getCandidatePhone();
        this.status = interview.getStatus();
        this.startedAt = interview.getStartedAt();
        this.finishedAt = interview.getFinishedAt();
        this.fraudScore = interview.getFraudScore();
        this.matchingScore = interview.getMatchingScore();
        this.consentGiven = interview.getConsentGiven();
        this.consentTimestamp = interview.getConsentTimestamp();
        this.createdAt = interview.getCreatedAt();
    }
}

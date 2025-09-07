package com.example.hr_assistant.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
public class InterviewReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;

    private LocalDateTime interviewDate;

    private double matchPercentage;

    @ElementCollection
    @CollectionTable(name = "report_competency_scores", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "competency_name")
    @Column(name = "score")
    private Map<String, Double> competencyScores = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "report_strengths", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "strength")
    private List<String> strengths = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "report_weaknesses", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "weakness")
    private List<String> weaknesses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private RecommendationStatus recommendation;

    @Lob
    private String recommendationReason;

    @Lob
    private String fullTranscript;
}

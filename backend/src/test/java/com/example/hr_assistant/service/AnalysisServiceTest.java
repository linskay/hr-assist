package com.example.hr_assistant.service;

import com.example.hr_assistant.model.Candidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class AnalysisServiceTest {

    private AnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new AnalysisService();
    }

    @Test
    void testAnalyzeResume_shouldExtractSkillsAndExperience() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setResumeText("Experienced Java developer with 5 years experience. Proficient in Spring and SQL. Also have some experience with python.");

        // When
        Candidate analyzedCandidate = analysisService.analyzeResume(candidate);

        // Then
        assertEquals(5, analyzedCandidate.getExtractedExperienceYears());
        List<String> skills = analyzedCandidate.getExtractedSkills();
        assertNotNull(skills);
        assertEquals(4, skills.size());
        assertTrue(skills.contains("java"));
        assertTrue(skills.contains("spring"));
        assertTrue(skills.contains("sql"));
        assertTrue(skills.contains("python"));
        assertFalse(skills.contains("react"));
    }

    @Test
    void testAnalyzeResume_shouldHandleNoExperienceFound() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setResumeText("I am a junior developer, skilled in Java.");

        // When
        Candidate analyzedCandidate = analysisService.analyzeResume(candidate);

        // Then
        assertEquals(0, analyzedCandidate.getExtractedExperienceYears());
        assertTrue(analyzedCandidate.getExtractedSkills().contains("java"));
    }

    @Test
    void testAnalyzeResume_shouldHandleEmptyResume() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setResumeText("");

        // When
        Candidate analyzedCandidate = analysisService.analyzeResume(candidate);

        // Then
        assertEquals(0, analyzedCandidate.getExtractedExperienceYears());
        assertTrue(analyzedCandidate.getExtractedSkills().isEmpty());
    }
}

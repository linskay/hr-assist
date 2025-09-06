package com.example.hr_assistant.service;

import com.example.hr_assistant.model.*;
import com.example.hr_assistant.repository.InterviewReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import com.example.hr_assistant.model.dto.InterviewStateDto;
import com.example.hr_assistant.model.dto.MessageDto;
import com.example.hr_assistant.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceTest {

    @Mock
    private InterviewReportRepository reportRepository;

    @Mock
    private VacancyRepository vacancyRepository;

    @InjectMocks
    private InterviewService interviewService;

    @Test
    void testGenerateReport_highMatch() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setExtractedSkills(List.of("java", "spring", "sql"));

        Vacancy vacancy = new Vacancy();
        vacancy.setRequiredCompetencies(Map.of("java", 0.5, "spring", 0.3, "sql", 0.2));

        when(reportRepository.save(any(InterviewReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InterviewReport report = interviewService.generateReport(candidate, vacancy, Collections.emptyList());

        // Then
        assertNotNull(report);
        assertEquals(100.0, report.getMatchPercentage());
        assertEquals(3, report.getStrengths().size());
        assertTrue(report.getWeaknesses().isEmpty());
        assertEquals(RecommendationStatus.PROCEED_TO_NEXT_STEP, report.getRecommendation());
    }

    @Test
    void testGenerateReport_partialMatch() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setExtractedSkills(List.of("java", "sql")); // Missing "spring"
        Vacancy vacancy = new Vacancy();
        vacancy.setRequiredCompetencies(Map.of("java", 0.5, "spring", 0.3, "sql", 0.2));
        when(reportRepository.save(any(InterviewReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InterviewReport report = interviewService.generateReport(candidate, vacancy, Collections.emptyList());

        // Then
        assertNotNull(report);
        assertEquals(70.0, report.getMatchPercentage(), 0.01); // 50 + 20
        assertEquals(2, report.getStrengths().size());
        assertEquals(1, report.getWeaknesses().size());
        assertTrue(report.getWeaknesses().contains("spring"));
        assertEquals(RecommendationStatus.NEEDS_FURTHER_REVIEW, report.getRecommendation());
    }

    @Test
    void testGenerateReport_lowMatch() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setExtractedSkills(List.of("python")); // Has none of the required skills
        Vacancy vacancy = new Vacancy();
        vacancy.setRequiredCompetencies(Map.of("java", 0.5, "spring", 0.3, "sql", 0.2));
        when(reportRepository.save(any(InterviewReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InterviewReport report = interviewService.generateReport(candidate, vacancy, Collections.emptyList());

        // Then
        assertNotNull(report);
        assertEquals(0.0, report.getMatchPercentage());
        assertTrue(report.getStrengths().isEmpty());
        assertEquals(3, report.getWeaknesses().size());
        assertEquals(RecommendationStatus.REJECT, report.getRecommendation());
    }

    @Test
    void testGenerateReport_skillFoundInTranscript() {
        // Given
        Candidate candidate = new Candidate();
        candidate.setExtractedSkills(List.of("java")); // Missing spring and sql
        Vacancy vacancy = new Vacancy();
        vacancy.setRequiredCompetencies(Map.of("java", 0.5, "spring", 0.3, "sql", 0.2));
        List<MessageDto> messages = List.of(
            new MessageDto("assistant", "Tell me about spring"),
            new MessageDto("user", "I have a lot of experience with Spring Boot.")
        );
        when(reportRepository.save(any(InterviewReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InterviewReport report = interviewService.generateReport(candidate, vacancy, messages);

        // Then
        assertNotNull(report);
        assertEquals(80.0, report.getMatchPercentage(), 0.01); // 50 (java) + 30 (spring)
        assertEquals(2, report.getStrengths().size());
        assertTrue(report.getStrengths().contains("java"));
        assertTrue(report.getStrengths().contains("spring"));
        assertEquals(1, report.getWeaknesses().size());
        assertTrue(report.getWeaknesses().contains("sql"));
    }

    @Test
    void testGetNextQuestion_asksAboutFirstSkill() {
        // Given
        Vacancy vacancy = new Vacancy();
        Map<String, Double> competencies = new java.util.LinkedHashMap<>();
        competencies.put("java", 0.5);
        competencies.put("spring", 0.3);
        vacancy.setRequiredCompetencies(competencies);
        InterviewStateDto state = new InterviewStateDto();
        state.setVacancyId(1L);
        state.setMessages(List.of(new MessageDto("assistant", "Здравствуйте!")));
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        // When
        String nextQuestion = interviewService.getNextQuestion(state);

        // Then
        assertTrue(nextQuestion.contains("java"));
    }

    @Test
    void testGetNextQuestion_asksAboutSecondSkill() {
        // Given
        Vacancy vacancy = new Vacancy();
        Map<String, Double> competencies = new java.util.LinkedHashMap<>();
        competencies.put("java", 0.5);
        competencies.put("spring", 0.3);
        vacancy.setRequiredCompetencies(competencies);
        InterviewStateDto state = new InterviewStateDto();
        state.setVacancyId(1L);
        state.setMessages(List.of(
            new MessageDto("assistant", "Здравствуйте!"),
            new MessageDto("user", "blah blah"),
            new MessageDto("assistant", "Расскажите, пожалуйста, о вашем опыте работы с java?")
        ));
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        // When
        String nextQuestion = interviewService.getNextQuestion(state);

        // Then
        assertTrue(nextQuestion.contains("spring"));
    }

    @Test
    void testGetNextQuestion_concludesInterview() {
        // Given
        Vacancy vacancy = new Vacancy();
        Map<String, Double> competencies = new java.util.LinkedHashMap<>();
        competencies.put("java", 0.5);
        competencies.put("spring", 0.3);
        vacancy.setRequiredCompetencies(competencies);
        InterviewStateDto state = new InterviewStateDto();
        state.setVacancyId(1L);
        state.setMessages(List.of(
            new MessageDto("assistant", "Расскажите, пожалуйста, о вашем опыте работы с java?"),
            new MessageDto("user", "blah blah"),
            new MessageDto("assistant", "Расскажите, пожалуйста, о вашем опыте работы с spring?")
        ));
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        // When
        String nextQuestion = interviewService.getNextQuestion(state);

        // Then
        assertTrue(nextQuestion.contains("На этом у меня все вопросы"));
    }
}

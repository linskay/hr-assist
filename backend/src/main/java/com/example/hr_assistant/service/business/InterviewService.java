package com.example.hr_assistant.service.business;

import com.example.hr_assistant.model.*;
import com.example.hr_assistant.repository.InterviewReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.hr_assistant.model.dto.InterviewStateDto;
import com.example.hr_assistant.repository.VacancyRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewReportRepository reportRepository;
    private final VacancyRepository vacancyRepository;

    /**
     * Calculates the match score of a candidate for a given vacancy.
     *
     * @param candidate The candidate.
     * @param vacancy The vacancy.
     * @param messages The transcript of the interview.
     * @return A map of competency scores.
     */
    private Map<String, Double> calculateScores(Candidate candidate, Vacancy vacancy, List<com.example.hr_assistant.model.dto.MessageDto> messages) {
        Map<String, Double> scores = new HashMap<>();
        double totalWeightedScore = 0.0;

        String userTranscript = messages.stream()
                .filter(m -> "user".equals(m.getAuthor()))
                .map(com.example.hr_assistant.model.dto.MessageDto::getText)
                .collect(Collectors.joining(" "))
                .toLowerCase();

        for (Map.Entry<String, Double> requirement : vacancy.getRequiredCompetencies().entrySet()) {
            String requiredSkill = requirement.getKey();
            Double weight = requirement.getValue();

            boolean hasSkillInResume = candidate.getExtractedSkills().stream()
                    .anyMatch(skill -> skill.equalsIgnoreCase(requiredSkill));

            boolean hasSkillInTranscript = userTranscript.contains(requiredSkill.toLowerCase());

            if (hasSkillInResume || hasSkillInTranscript) {
                scores.put(requiredSkill, 100.0 * weight); // Full points if skill is present
                totalWeightedScore += 100.0 * weight;
            } else {
                scores.put(requiredSkill, 0.0); // Zero points if skill is absent
            }
        }
        // The overall percentage is the sum of weighted scores
        scores.put("overallMatchPercentage", totalWeightedScore);
        return scores;
    }

    /**
     * Generates a final report for a candidate and vacancy.
     *
     * @param candidate The candidate who was interviewed.
     * @param vacancy The vacancy they were interviewed for.
     * @param messages The transcript of the interview.
     * @return The generated InterviewReport.
     */
    @Transactional
    public InterviewReport generateReport(Candidate candidate, Vacancy vacancy, List<com.example.hr_assistant.model.dto.MessageDto> messages) {
        InterviewReport report = new InterviewReport();
        report.setCandidate(candidate);
        report.setVacancy(vacancy);
        report.setInterviewDate(LocalDateTime.now());

        String fullTranscript = messages.stream()
                .map(m -> m.getAuthor() + ": " + m.getText())
                .collect(Collectors.joining("\n"));
        report.setFullTranscript(fullTranscript);

        Map<String, Double> scores = calculateScores(candidate, vacancy, messages);
        double matchPercentage = scores.remove("overallMatchPercentage");

        report.setMatchPercentage(matchPercentage);
        report.setCompetencyScores(scores);

        List<String> strengths = scores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        report.setStrengths(strengths);

        List<String> weaknesses = scores.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        report.setWeaknesses(weaknesses);

        // Basic recommendation logic
        if (matchPercentage > 70) {
            report.setRecommendation(RecommendationStatus.PROCEED_TO_NEXT_STEP);
            report.setRecommendationReason("High compliance with required competencies based on resume analysis.");
        } else if (matchPercentage > 40) {
            report.setRecommendation(RecommendationStatus.NEEDS_FURTHER_REVIEW);
            report.setRecommendationReason("Partial compliance. Further review of skills is recommended.");
        } else {
            report.setRecommendation(RecommendationStatus.REJECT);
            report.setRecommendationReason("Low compliance with key required competencies.");
        }

        return reportRepository.save(report);
    }

    /**
     * Determines the next question to ask based on the interview state.
     * Basic placeholder logic for now.
     * @param state The current state of the interview.
     * @return The next question for the assistant to ask.
     */
    public String getNextQuestion(InterviewStateDto state) {
        Optional<Vacancy> vacancyOpt = vacancyRepository.findById(state.getVacancyId());
        if (vacancyOpt.isEmpty()) {
            return "Извините, я не могу найти информацию о данной вакансии.";
        }
        Vacancy vacancy = vacancyOpt.get();

        String lastAssistantQuestion = state.getMessages().stream()
                .filter(m -> "assistant".equals(m.getAuthor()))
                .map(com.example.hr_assistant.model.dto.MessageDto::getText)
                .reduce((first, second) -> second)
                .orElse("");

        // Find the next skill to ask about
        for (String skill : vacancy.getRequiredCompetencies().keySet()) {
            boolean skillMentioned = lastAssistantQuestion.toLowerCase().contains(skill.toLowerCase());
            if (!skillMentioned) {
                // Find if we have already asked about this skill
                boolean alreadyAsked = state.getMessages().stream()
                        .filter(m -> "assistant".equals(m.getAuthor()))
                        .anyMatch(m -> m.getText().toLowerCase().contains(skill.toLowerCase()));

                if (!alreadyAsked) {
                    return "Хорошо, спасибо. Расскажите, пожалуйста, о вашем опыте работы с " + skill + "?";
                }
            }
        }

        // If all skills are discussed
        return "Спасибо за ваши ответы. На этом у меня все вопросы. Мы свяжемся с вами в ближайшее время.";
    }
}

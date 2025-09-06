package com.example.hr_assistant.service;

import com.example.hr_assistant.model.Candidate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private static final List<String> KNOWN_SKILLS = Arrays.asList(
            "java", "python", "sql", "spring", "react", "angular", "vue",
            "javascript", "typescript", "html", "css", "docker", "kubernetes",
            "aws", "azure", "gcp", "git", "jira", "nlp", "machine learning"
    );

    /**
     * Analyzes the resume text of a candidate to extract skills and years of experience.
     * This is a basic implementation using keyword matching and regex.
     *
     * @param candidate The candidate to analyze.
     * @return The candidate with extracted information populated.
     */
    public Candidate analyzeResume(Candidate candidate) {
        if (candidate.getResumeText() == null || candidate.getResumeText().isBlank()) {
            return candidate;
        }

        String resumeText = candidate.getResumeText().toLowerCase();

        // Extract skills
        List<String> foundSkills = KNOWN_SKILLS.stream()
                .filter(skill -> resumeText.contains(skill))
                .collect(Collectors.toList());
        candidate.setExtractedSkills(foundSkills);

        // Extract experience
        Pattern experiencePattern = Pattern.compile("(\\d+)\\s+(?:year|yr|лет|год|года)s?\\s+experience");
        Matcher matcher = experiencePattern.matcher(resumeText);
        if (matcher.find()) {
            try {
                int years = Integer.parseInt(matcher.group(1));
                candidate.setExtractedExperienceYears(years);
            } catch (NumberFormatException e) {
                // Ignore if the number is not parsable
            }
        }

        return candidate;
    }
}

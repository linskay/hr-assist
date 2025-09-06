package com.example.hr_assistant.controller;

import com.example.hr_assistant.exception.ResourceNotFoundException;
import com.example.hr_assistant.model.Candidate;
import com.example.hr_assistant.model.InterviewReport;
import com.example.hr_assistant.model.Vacancy;
import com.example.hr_assistant.model.dto.InterviewStateDto;
import com.example.hr_assistant.model.dto.NextQuestionDto;
import com.example.hr_assistant.service.CandidateService;
import com.example.hr_assistant.service.InterviewService;
import com.example.hr_assistant.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final CandidateService candidateService;
    private final VacancyService vacancyService;

    @PostMapping("/generate-report")
    public ResponseEntity<InterviewReport> generateReport(@RequestBody InterviewStateDto interviewState) {
        Candidate candidate = candidateService.getCandidateById(interviewState.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + interviewState.getCandidateId()));

        Vacancy vacancy = vacancyService.getVacancyById(interviewState.getVacancyId())
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found with id: " + interviewState.getVacancyId()));

        InterviewReport report = interviewService.generateReport(candidate, vacancy, interviewState.getMessages());
        return ResponseEntity.ok(report);
    }

    @PostMapping("/next-question")
    public ResponseEntity<NextQuestionDto> getNextQuestion(@RequestBody InterviewStateDto interviewState) {
        String nextQuestion = interviewService.getNextQuestion(interviewState);
        return ResponseEntity.ok(new NextQuestionDto(nextQuestion));
    }
}

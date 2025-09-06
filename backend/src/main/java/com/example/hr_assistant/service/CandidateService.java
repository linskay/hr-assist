package com.example.hr_assistant.service;

import com.example.hr_assistant.model.Candidate;
import com.example.hr_assistant.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final AnalysisService analysisService;

    @Transactional(readOnly = true)
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }

    @Transactional
    public Candidate createCandidate(Candidate candidate) {
        // First, analyze the resume to extract structured data
        Candidate analyzedCandidate = analysisService.analyzeResume(candidate);
        // Then, save the enriched candidate
        return candidateRepository.save(analyzedCandidate);
    }

    @Transactional
    public Optional<Candidate> updateCandidate(Long id, Candidate candidateDetails) {
        return candidateRepository.findById(id)
                .map(existingCandidate -> {
                    existingCandidate.setName(candidateDetails.getName());
                    existingCandidate.setEmail(candidateDetails.getEmail());
                    existingCandidate.setPhone(candidateDetails.getPhone());
                    existingCandidate.setResumeText(candidateDetails.getResumeText());
                    // Re-analyze on update
                    Candidate analyzedCandidate = analysisService.analyzeResume(existingCandidate);
                    return candidateRepository.save(analyzedCandidate);
                });
    }

    @Transactional
    public boolean deleteCandidate(Long id) {
        if (candidateRepository.existsById(id)) {
            candidateRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

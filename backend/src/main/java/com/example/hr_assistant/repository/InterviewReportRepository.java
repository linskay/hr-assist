package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.InterviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewReportRepository extends JpaRepository<InterviewReport, Long> {
}

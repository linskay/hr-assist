package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.Competency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetencyRepository extends JpaRepository<Competency, Long> {
}

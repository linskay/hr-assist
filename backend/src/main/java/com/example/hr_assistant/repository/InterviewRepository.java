package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с интервью
 */
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    
    List<Interview> findByVacancyId(Long vacancyId);
    
    List<Interview> findByStatus(Interview.InterviewStatus status);
    
    @Query("SELECT i FROM Interview i WHERE i.candidateEmail = :email")
    List<Interview> findByCandidateEmail(@Param("email") String email);
    
    @Query("SELECT i FROM Interview i WHERE i.createdAt BETWEEN :startDate AND :endDate")
    List<Interview> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT i FROM Interview i WHERE i.fraudScore > :threshold")
    List<Interview> findSuspiciousInterviews(@Param("threshold") Double threshold);
    
    Optional<Interview> findByIdAndStatus(Long id, Interview.InterviewStatus status);
}

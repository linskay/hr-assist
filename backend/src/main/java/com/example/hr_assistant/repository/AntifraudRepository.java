package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.Antifraud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с антифрод данными
 */
@Repository
public interface AntifraudRepository extends JpaRepository<Antifraud, Long> {
    
    Optional<Antifraud> findByInterviewId(Long interviewId);
    
    @Query("SELECT a FROM Antifraud a WHERE a.overallFraudScore > :threshold")
    List<Antifraud> findSuspiciousInterviews(@Param("threshold") Double threshold);
    
    @Query("SELECT a FROM Antifraud a WHERE a.fraudStatus = :status")
    List<Antifraud> findByFraudStatus(@Param("status") Antifraud.FraudStatus status);
}

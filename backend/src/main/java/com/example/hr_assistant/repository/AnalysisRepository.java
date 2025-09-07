package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с анализом интервью
 */
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    
    Optional<Analysis> findByInterviewId(Long interviewId);
    
    @Query("SELECT a FROM Analysis a WHERE a.overallScore >= :minScore")
    List<Analysis> findByMinScore(@Param("minScore") Double minScore);
    
    @Query("SELECT a FROM Analysis a WHERE a.recommendation = :recommendation")
    List<Analysis> findByRecommendation(@Param("recommendation") String recommendation);
}

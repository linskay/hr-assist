package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.Recording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с записями
 */
@Repository
public interface RecordingRepository extends JpaRepository<Recording, Long> {
    
    List<Recording> findByInterviewId(Long interviewId);
    
    List<Recording> findByInterviewIdAndQuestionId(Long interviewId, Long questionId);
    
    @Query("SELECT r FROM Recording r WHERE r.interview.id = :interviewId AND r.isFinalChunk = true")
    List<Recording> findFinalRecordingsByInterviewId(@Param("interviewId") Long interviewId);
    
    @Query("SELECT COUNT(r) FROM Recording r WHERE r.interview.id = :interviewId")
    Long countByInterviewId(@Param("interviewId") Long interviewId);
}

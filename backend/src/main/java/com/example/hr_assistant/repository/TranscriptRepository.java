package com.example.hr_assistant.repository;

import com.example.hr_assistant.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с транскрипциями
 */
@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
    
    Optional<Transcript> findByRecordingId(Long recordingId);
    
    @Query("SELECT t FROM Transcript t WHERE t.recording.interview.id = :interviewId")
    List<Transcript> findByInterviewId(@Param("interviewId") Long interviewId);
    
    @Query("SELECT t FROM Transcript t WHERE t.recording.interview.id = :interviewId AND t.recording.question.id = :questionId")
    Optional<Transcript> findByInterviewIdAndQuestionId(@Param("interviewId") Long interviewId, 
                                                       @Param("questionId") Long questionId);
}

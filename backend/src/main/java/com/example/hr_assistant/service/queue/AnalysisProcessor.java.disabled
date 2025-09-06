package com.example.hr_assistant.service.queue;

import com.example.hr_assistant.model.Analysis;
import com.example.hr_assistant.model.Interview;
import com.example.hr_assistant.model.Vacancy;
import com.example.hr_assistant.model.dto.QueueMessage;
import com.example.hr_assistant.repository.AnalysisRepository;
import com.example.hr_assistant.repository.InterviewRepository;
import com.example.hr_assistant.service.ml.ClassificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Обработчик задач анализа интервью
 */
@Service
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.auto-startup", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class AnalysisProcessor {

    private final ClassificationService classificationService;
    private final InterviewRepository interviewRepository;
    private final AnalysisRepository analysisRepository;
    private final QueueService queueService;
    private final ObjectMapper objectMapper;

    // @RabbitListener(queues = QueueService.ANALYSIS_QUEUE)
    public void processAnalysisTask(String message) {
        try {
            QueueMessage queueMessage = objectMapper.readValue(message, QueueMessage.class);
            
            log.info("Обработка задачи анализа: interviewId={}", queueMessage.getInterviewId());
            
            // Получаем интервью
            Interview interview = interviewRepository.findById(queueMessage.getInterviewId())
                .orElseThrow(() -> new RuntimeException("Интервью не найдено: " + queueMessage.getInterviewId()));
            
            Vacancy vacancy = interview.getVacancy();
            String transcriptText = queueMessage.getTranscriptText();
            
            // Выполняем классификацию соответствия
            // Преобразуем Map<String, Double> в Map<String, String> для совместимости
            Map<String, String> requirementsAsString = new HashMap<>();
            vacancy.getRequiredCompetencies().forEach((key, value) -> 
                requirementsAsString.put(key, value.toString())
            );
            
            Map<String, Double> competencyScores = classificationService.classifyCompliance(
                transcriptText, 
                requirementsAsString
            );
            
            // Вычисляем общую оценку
            double overallScore = classificationService.calculateOverallScore(
                competencyScores, 
                vacancy.getRequiredCompetencies()
            );
            
            // Создаем анализ
            Analysis analysis = new Analysis();
            analysis.setInterview(interview);
            analysis.setOverallScore(overallScore);
            analysis.setCompetencyScoresJson(serializeMap(competencyScores));
            analysis.setDetailsJson(createAnalysisDetails(competencyScores, transcriptText));
            
            // Определяем рекомендацию
            String recommendation = determineRecommendation(overallScore);
            analysis.setRecommendation(recommendation);
            analysis.setRecommendationReason(createRecommendationReason(overallScore, competencyScores));
            
            // Сохраняем анализ
            analysisRepository.save(analysis);
            
            // Обновляем интервью
            interview.setMatchingScore(overallScore);
            interviewRepository.save(interview);
            
            // Отправляем уведомление о завершении анализа
            queueService.sendNotification(
                interview.getId(),
                "ANALYSIS_COMPLETED",
                "Анализ интервью завершен. Оценка: " + String.format("%.1f%%", overallScore * 100)
            );
            
            log.info("Анализ завершен: interviewId={}, score={}", 
                queueMessage.getInterviewId(), overallScore);
            
        } catch (Exception e) {
            log.error("Ошибка при обработке анализа: {}", e.getMessage(), e);
            
            // Отправляем уведомление об ошибке
            try {
                QueueMessage queueMessage = objectMapper.readValue(message, QueueMessage.class);
                queueService.sendNotification(
                    queueMessage.getInterviewId(),
                    "ANALYSIS_ERROR",
                    "Ошибка анализа: " + e.getMessage()
                );
            } catch (Exception ex) {
                log.error("Ошибка при отправке уведомления об ошибке: {}", ex.getMessage());
            }
        }
    }

    /**
     * Определяет рекомендацию на основе оценки
     */
    private String determineRecommendation(double overallScore) {
        if (overallScore >= 0.7) {
            return "PROCEED_TO_NEXT_STEP";
        } else if (overallScore >= 0.4) {
            return "NEEDS_FURTHER_REVIEW";
        } else {
            return "REJECT";
        }
    }

    /**
     * Создает обоснование рекомендации
     */
    private String createRecommendationReason(double overallScore, Map<String, Double> competencyScores) {
        StringBuilder reason = new StringBuilder();
        
        if (overallScore >= 0.7) {
            reason.append("Высокое соответствие требованиям (");
            reason.append(String.format("%.1f%%", overallScore * 100));
            reason.append("). ");
        } else if (overallScore >= 0.4) {
            reason.append("Частичное соответствие требованиям (");
            reason.append(String.format("%.1f%%", overallScore * 100));
            reason.append("). ");
        } else {
            reason.append("Низкое соответствие требованиям (");
            reason.append(String.format("%.1f%%", overallScore * 100));
            reason.append("). ");
        }
        
        // Добавляем детали по компетенциям
        long strongCompetencies = competencyScores.values().stream()
            .mapToLong(score -> score >= 0.7 ? 1 : 0)
            .sum();
        
        long weakCompetencies = competencyScores.values().stream()
            .mapToLong(score -> score < 0.3 ? 1 : 0)
            .sum();
        
        reason.append("Сильные стороны: ").append(strongCompetencies).append(" компетенций. ");
        reason.append("Требуют развития: ").append(weakCompetencies).append(" компетенций.");
        
        return reason.toString();
    }

    /**
     * Создает детали анализа
     */
    private String createAnalysisDetails(Map<String, Double> competencyScores, String transcriptText) {
        Map<String, Object> details = new HashMap<>();
        details.put("competency_scores", competencyScores);
        details.put("transcript_length", transcriptText.length());
        details.put("analysis_timestamp", System.currentTimeMillis());
        
        // Анализ ключевых слов
        Map<String, Integer> keywordCounts = analyzeKeywords(transcriptText, competencyScores.keySet());
        details.put("keyword_analysis", keywordCounts);
        
        try {
            return objectMapper.writeValueAsString(details);
        } catch (Exception e) {
            log.error("Ошибка при сериализации деталей анализа: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * Анализирует ключевые слова в транскрипте
     */
    private Map<String, Integer> analyzeKeywords(String transcriptText, java.util.Set<String> competencies) {
        Map<String, Integer> keywordCounts = new HashMap<>();
        String lowerText = transcriptText.toLowerCase();
        
        for (String competency : competencies) {
            String lowerCompetency = competency.toLowerCase();
            int count = 0;
            
            // Подсчитываем упоминания компетенции
            int index = 0;
            while ((index = lowerText.indexOf(lowerCompetency, index)) != -1) {
                count++;
                index += lowerCompetency.length();
            }
            
            keywordCounts.put(competency, count);
        }
        
        return keywordCounts;
    }

    /**
     * Сериализует Map в JSON
     */
    private String serializeMap(Map<String, Double> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Ошибка при сериализации Map: {}", e.getMessage());
            return "{}";
        }
    }
}

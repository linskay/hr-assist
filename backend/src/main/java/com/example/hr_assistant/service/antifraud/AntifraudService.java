package com.example.hr_assistant.service.antifraud;

import com.example.hr_assistant.config.AntifraudConfig;
import com.example.hr_assistant.model.Antifraud;
import com.example.hr_assistant.model.Interview;
import com.example.hr_assistant.model.dto.HeartbeatRequest;
import com.example.hr_assistant.repository.AntifraudRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Основной сервис антифрод системы
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AntifraudService {

    private final AntifraudConfig antifraudConfig;
    private final AntifraudRepository antifraudRepository;
    private final LivenessService livenessService;
    private final FaceRecognitionService faceRecognitionService;
    private final VoiceVerificationService voiceVerificationService;
    private final AiTextDetectionService aiTextDetectionService;
    private final ObjectMapper objectMapper;

    /**
     * Создает антифрод запись для интервью
     */
    public Antifraud createAntifraudRecord(Interview interview) {
        Antifraud antifraud = new Antifraud();
        antifraud.setInterview(interview);
        antifraud.setFraudStatus(Antifraud.FraudStatus.CLEAN);
        antifraud.setVisibilityEventsCount(0);
        antifraud.setHeartbeatGaps(0);
        antifraud.setDevtoolsDetected(false);
        antifraud.setTabSwitchesCount(0);
        antifraud.setWindowBlurCount(0);
        
        return antifraudRepository.save(antifraud);
    }

    /**
     * Обновляет heartbeat данные
     */
    public void updateHeartbeat(Long interviewId, HeartbeatRequest heartbeat) {
        try {
            Antifraud antifraud = antifraudRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new RuntimeException("Antifraud запись не найдена для интервью: " + interviewId));

            // Анализируем browser events
            analyzeBrowserEvents(antifraud, heartbeat);
            
            // Проверяем на devtools
            boolean devtoolsDetected = detectDevtools(heartbeat);
            if (devtoolsDetected) {
                antifraud.setDevtoolsDetected(true);
            }
            
            antifraudRepository.save(antifraud);
            
        } catch (Exception e) {
            log.error("Ошибка при обновлении heartbeat: {}", e.getMessage(), e);
        }
    }

    /**
     * Выполняет liveness проверку
     */
    public void performLivenessCheck(Long interviewId, byte[] videoData) {
        try {
            Antifraud antifraud = antifraudRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new RuntimeException("Antifraud запись не найдена"));

            LivenessService.LivenessResult result = livenessService.checkLiveness(videoData);
            
            antifraud.setLivenessScore(result.getLivenessScore());
            
            // Обновляем флаги
            Map<String, Object> flags = getOrCreateFlags(antifraud);
            flags.put("liveness", result.getFlags());
            antifraud.setFlagsJson(serializeFlags(flags));
            
            antifraudRepository.save(antifraud);
            
            log.info("Liveness проверка для интервью {}: score={}", interviewId, result.getLivenessScore());
            
        } catch (Exception e) {
            log.error("Ошибка при liveness проверке: {}", e.getMessage(), e);
        }
    }

    /**
     * Выполняет face match проверку
     */
    public void performFaceMatchCheck(Long interviewId, byte[] candidateImage, byte[] referenceImage) {
        try {
            Antifraud antifraud = antifraudRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new RuntimeException("Antifraud запись не найдена"));

            FaceRecognitionService.FaceMatchResult result = 
                faceRecognitionService.verifyFaceMatch(candidateImage, referenceImage);
            
            antifraud.setFaceMatchScore(result.getFaceMatchScore());
            
            // Обновляем флаги
            Map<String, Object> flags = getOrCreateFlags(antifraud);
            flags.put("face_match", result.getFlags());
            antifraud.setFlagsJson(serializeFlags(flags));
            
            antifraudRepository.save(antifraud);
            
            log.info("Face match проверка для интервью {}: score={}", interviewId, result.getFaceMatchScore());
            
        } catch (Exception e) {
            log.error("Ошибка при face match проверке: {}", e.getMessage(), e);
        }
    }

    /**
     * Выполняет voice verification проверку
     */
    public void performVoiceVerificationCheck(Long interviewId, byte[] candidateAudio, byte[] referenceAudio) {
        try {
            Antifraud antifraud = antifraudRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new RuntimeException("Antifraud запись не найдена"));

            VoiceVerificationService.VoiceMatchResult result = 
                voiceVerificationService.verifyVoiceMatch(candidateAudio, referenceAudio);
            
            antifraud.setVoiceMatchScore(result.getVoiceMatchScore());
            
            // Обновляем флаги
            Map<String, Object> flags = getOrCreateFlags(antifraud);
            flags.put("voice_match", result.getFlags());
            flags.put("synthetic_speech", result.getIsSynthetic());
            antifraud.setFlagsJson(serializeFlags(flags));
            
            antifraudRepository.save(antifraud);
            
            log.info("Voice verification для интервью {}: score={}, synthetic={}", 
                interviewId, result.getVoiceMatchScore(), result.getIsSynthetic());
            
        } catch (Exception e) {
            log.error("Ошибка при voice verification: {}", e.getMessage(), e);
        }
    }

    /**
     * Выполняет AI text detection
     */
    public void performAiTextDetection(Long interviewId, String text) {
        try {
            Antifraud antifraud = antifraudRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new RuntimeException("Antifraud запись не найдена"));

            AiTextDetectionService.AiDetectionResult result = aiTextDetectionService.detectAiText(text);
            
            antifraud.setTextAiScore(result.getAiProbability());
            
            // Обновляем флаги
            Map<String, Object> flags = getOrCreateFlags(antifraud);
            flags.put("ai_text_detection", result.getFlags());
            antifraud.setFlagsJson(serializeFlags(flags));
            
            antifraudRepository.save(antifraud);
            
            log.info("AI text detection для интервью {}: probability={}", interviewId, result.getAiProbability());
            
        } catch (Exception e) {
            log.error("Ошибка при AI text detection: {}", e.getMessage(), e);
        }
    }

    /**
     * Вычисляет общий fraud score
     */
    public void calculateFraudScore(Long interviewId) {
        try {
            Antifraud antifraud = antifraudRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new RuntimeException("Antifraud запись не найдена"));

            AntifraudConfig.Weights weights = antifraudConfig.getWeights();
            
            double fraudScore = 0.0;
            
            // Liveness score (чем ниже, тем больше fraud)
            if (antifraud.getLivenessScore() != null) {
                fraudScore += weights.getLiveness() * (1.0 - antifraud.getLivenessScore());
            }
            
            // Face match score (чем ниже, тем больше fraud)
            if (antifraud.getFaceMatchScore() != null) {
                fraudScore += weights.getFaceMatch() * (1.0 - antifraud.getFaceMatchScore());
            }
            
            // Voice match score (чем ниже, тем больше fraud)
            if (antifraud.getVoiceMatchScore() != null) {
                fraudScore += weights.getVoiceMatch() * (1.0 - antifraud.getVoiceMatchScore());
            }
            
            // AI text score (чем выше, тем больше fraud)
            if (antifraud.getTextAiScore() != null) {
                fraudScore += weights.getTextAi() * antifraud.getTextAiScore();
            }
            
            // Visibility penalty
            if (antifraud.getVisibilityEventsCount() > 0) {
                double visibilityPenalty = Math.min(1.0, antifraud.getVisibilityEventsCount() / 10.0);
                fraudScore += weights.getVisibility() * visibilityPenalty;
            }
            
            // Devtools penalty
            if (antifraud.getDevtoolsDetected()) {
                fraudScore += weights.getDevtools();
            }
            
            antifraud.setOverallFraudScore(fraudScore);
            
            // Определяем статус
            AntifraudConfig.Thresholds thresholds = antifraudConfig.getThresholds();
            if (fraudScore >= thresholds.getReject()) {
                antifraud.setFraudStatus(Antifraud.FraudStatus.FRAUD_DETECTED);
            } else if (fraudScore >= thresholds.getReview()) {
                antifraud.setFraudStatus(Antifraud.FraudStatus.MANUAL_REVIEW);
            } else {
                antifraud.setFraudStatus(Antifraud.FraudStatus.CLEAN);
            }
            
            antifraudRepository.save(antifraud);
            
            log.info("Fraud score для интервью {}: score={}, status={}", 
                interviewId, fraudScore, antifraud.getFraudStatus());
            
        } catch (Exception e) {
            log.error("Ошибка при вычислении fraud score: {}", e.getMessage(), e);
        }
    }

    /**
     * Анализирует browser events
     */
    private void analyzeBrowserEvents(Antifraud antifraud, HeartbeatRequest heartbeat) {
        if (heartbeat.getBrowserEvents() != null) {
            // Парсим browser events и обновляем счетчики
            String events = heartbeat.getBrowserEvents();
            
            if (events.contains("visibilitychange")) {
                antifraud.setVisibilityEventsCount(antifraud.getVisibilityEventsCount() + 1);
            }
            
            if (events.contains("blur")) {
                antifraud.setWindowBlurCount(antifraud.getWindowBlurCount() + 1);
            }
            
            if (events.contains("tab_switch")) {
                antifraud.setTabSwitchesCount(antifraud.getTabSwitchesCount() + 1);
            }
        }
    }

    /**
     * Детектирует devtools
     */
    private boolean detectDevtools(HeartbeatRequest heartbeat) {
        // Простая эвристика для детекции devtools
        // В реальной реализации нужны более сложные методы
        
        if (heartbeat.getWindowWidth() != null && heartbeat.getWindowHeight() != null) {
            // Проверяем на подозрительные размеры окна
            int width = heartbeat.getWindowWidth();
            int height = heartbeat.getWindowHeight();
            
            // Devtools обычно открываются сбоку или снизу
            return width < 800 || height < 600;
        }
        
        return false;
    }

    /**
     * Получает или создает флаги
     */
    private Map<String, Object> getOrCreateFlags(Antifraud antifraud) {
        try {
            if (antifraud.getFlagsJson() != null) {
                return objectMapper.readValue(antifraud.getFlagsJson(), Map.class);
            }
        } catch (JsonProcessingException e) {
            log.warn("Ошибка при парсинге флагов: {}", e.getMessage());
        }
        
        return new HashMap<>();
    }

    /**
     * Сериализует флаги в JSON
     */
    private String serializeFlags(Map<String, Object> flags) {
        try {
            return objectMapper.writeValueAsString(flags);
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации флагов: {}", e.getMessage());
            return "{}";
        }
    }
}

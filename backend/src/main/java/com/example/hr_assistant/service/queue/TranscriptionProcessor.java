package com.example.hr_assistant.service.queue;

import com.example.hr_assistant.model.Recording;
import com.example.hr_assistant.model.Transcript;
import com.example.hr_assistant.model.dto.QueueMessage;
import com.example.hr_assistant.repository.RecordingRepository;
import com.example.hr_assistant.repository.TranscriptRepository;
import com.example.hr_assistant.service.ml.AsrService;
import com.example.hr_assistant.service.storage.MediaStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Обработчик задач транскрипции
 */
@Service
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.auto-startup", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class TranscriptionProcessor {

    private final AsrService asrService;
    private final MediaStorageService mediaStorageService;
    private final RecordingRepository recordingRepository;
    private final TranscriptRepository transcriptRepository;
    private final QueueService queueService;
    private final ObjectMapper objectMapper;

    // @RabbitListener(queues = QueueService.TRANSCRIPTION_QUEUE)
    public void processTranscriptionTask(String message) {
        try {
            QueueMessage queueMessage = objectMapper.readValue(message, QueueMessage.class);
            
            log.info("Обработка задачи транскрипции: recordingId={}", queueMessage.getRecordingId());
            
            // Получаем запись
            Recording recording = recordingRepository.findById(queueMessage.getRecordingId())
                .orElseThrow(() -> new RuntimeException("Запись не найдена: " + queueMessage.getRecordingId()));
            
            // Загружаем аудио файл
            String fileName = extractFileNameFromUrl(queueMessage.getFileUrl());
            InputStream audioStream = mediaStorageService.getFile(fileName);
            byte[] audioData = audioStream.readAllBytes();
            
            // Выполняем транскрипцию
            Transcript transcript = asrService.transcribeAudio(audioData, "ru");
            transcript.setRecording(recording);
            
            // Сохраняем транскрипцию
            transcriptRepository.save(transcript);
            
            // Обновляем запись с результатами
            recording.setAsrConfidence(transcript.getAsrConfidence());
            recordingRepository.save(recording);
            
            // Отправляем задачу анализа
            queueService.sendAnalysisTask(
                recording.getInterview().getId(), 
                transcript.getText()
            );
            
            log.info("Транскрипция завершена: recordingId={}, confidence={}", 
                queueMessage.getRecordingId(), transcript.getAsrConfidence());
            
        } catch (Exception e) {
            log.error("Ошибка при обработке транскрипции: {}", e.getMessage(), e);
            
            // Отправляем уведомление об ошибке
            try {
                QueueMessage queueMessage = objectMapper.readValue(message, QueueMessage.class);
                queueService.sendNotification(
                    queueMessage.getInterviewId(),
                    "TRANSCRIPTION_ERROR",
                    "Ошибка транскрипции: " + e.getMessage()
                );
            } catch (Exception ex) {
                log.error("Ошибка при отправке уведомления об ошибке: {}", ex.getMessage());
            }
        }
    }

    /**
     * Извлекает имя файла из URL
     */
    private String extractFileNameFromUrl(String fileUrl) {
        // Извлекаем имя файла из URL
        // Например: http://localhost:9000/bucket/folder/file.wav -> folder/file.wav
        String[] parts = fileUrl.split("/");
        if (parts.length >= 3) {
            StringBuilder fileName = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                if (fileName.length() > 0) {
                    fileName.append("/");
                }
                fileName.append(parts[i]);
            }
            return fileName.toString();
        }
        throw new IllegalArgumentException("Некорректный URL файла: " + fileUrl);
    }
}

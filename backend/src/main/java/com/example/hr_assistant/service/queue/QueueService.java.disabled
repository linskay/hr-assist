package com.example.hr_assistant.service.queue;

import com.example.hr_assistant.model.dto.QueueMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с очередями RabbitMQ
 */
// @Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

    // private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    // Имена очередей
    public static final String TRANSCRIPTION_QUEUE = "transcription.queue";
    public static final String ANALYSIS_QUEUE = "analysis.queue";
    public static final String ANTIFRAUD_QUEUE = "antifraud.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    /**
     * Отправляет задачу транскрипции в очередь
     */
    public void sendTranscriptionTask(Long recordingId, String fileUrl) {
        log.warn("QueueService отключен - задача транскрипции не отправлена: recordingId={}", recordingId);
        // try {
        //     QueueMessage message = new QueueMessage();
        //     message.setType(QueueMessage.MessageType.TRANSCRIPTION);
        //     message.setRecordingId(recordingId);
        //     message.setFileUrl(fileUrl);
        //     message.setTimestamp(System.currentTimeMillis());

        //     String jsonMessage = objectMapper.writeValueAsString(message);
            
        //     rabbitTemplate.convertAndSend(TRANSCRIPTION_QUEUE, jsonMessage);
            
        //     log.info("Задача транскрипции отправлена в очередь: recordingId={}", recordingId);
            
        // } catch (Exception e) {
        //     log.error("Ошибка при отправке задачи транскрипции: {}", e.getMessage(), e);
        //     throw new RuntimeException("Ошибка отправки задачи транскрипции", e);
        // }
    }

    /**
     * Отправляет задачу анализа в очередь
     */
    public void sendAnalysisTask(Long interviewId, String transcriptText) {
        log.warn("QueueService отключен - задача анализа не отправлена: interviewId={}", interviewId);
        // try {
        //     QueueMessage message = new QueueMessage();
        //     message.setType(QueueMessage.MessageType.ANALYSIS);
        //     message.setInterviewId(interviewId);
        //     message.setTranscriptText(transcriptText);
        //     message.setTimestamp(System.currentTimeMillis());

        //     String jsonMessage = objectMapper.writeValueAsString(message);
            
        //     rabbitTemplate.convertAndSend(ANALYSIS_QUEUE, jsonMessage);
            
        //     log.info("Задача анализа отправлена в очередь: interviewId={}", interviewId);
            
        // } catch (Exception e) {
        //     log.error("Ошибка при отправке задачи анализа: {}", e.getMessage(), e);
        //     throw new RuntimeException("Ошибка отправки задачи анализа", e);
        // }
    }

    /**
     * Отправляет задачу антифрод проверки в очередь
     */
    public void sendAntifraudTask(Long interviewId, String taskType, Object data) {
        log.warn("QueueService отключен - задача антифрод не отправлена: interviewId={}, taskType={}", interviewId, taskType);
        // try {
        //     QueueMessage message = new QueueMessage();
        //     message.setType(QueueMessage.MessageType.ANTIFRAUD);
        //     message.setInterviewId(interviewId);
        //     message.setTaskType(taskType);
        //     message.setData(data);
        //     message.setTimestamp(System.currentTimeMillis());

        //     String jsonMessage = objectMapper.writeValueAsString(message);
        //     
        //     rabbitTemplate.convertAndSend(ANTIFRAUD_QUEUE, jsonMessage);
        //     
        //     log.info("Задача антифрод проверки отправлена в очередь: interviewId={}, taskType={}", 
        //         interviewId, taskType);
        //     
        // } catch (Exception e) {
        //     log.error("Ошибка при отправке задачи антифрод: {}", e.getMessage(), e);
        //     throw new RuntimeException("Ошибка отправки задачи антифрод", e);
        // }
    }

    /**
     * Отправляет уведомление в очередь
     */
    public void sendNotification(Long interviewId, String notificationType, String message) {
        log.warn("QueueService отключен - уведомление не отправлено: interviewId={}, type={}", interviewId, notificationType);
        // try {
        //     QueueMessage queueMessage = new QueueMessage();
        //     queueMessage.setType(QueueMessage.MessageType.NOTIFICATION);
        //     queueMessage.setInterviewId(interviewId);
        //     queueMessage.setNotificationType(notificationType);
        //     queueMessage.setMessage(message);
        //     queueMessage.setTimestamp(System.currentTimeMillis());

        //     String jsonMessage = objectMapper.writeValueAsString(queueMessage);
        //     
        //     rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE, jsonMessage);
        //     
        //     log.info("Уведомление отправлено в очередь: interviewId={}, type={}", 
        //         interviewId, notificationType);
        //     
        // } catch (Exception e) {
        //     log.error("Ошибка при отправке уведомления: {}", e.getMessage(), e);
        //     throw new RuntimeException("Ошибка отправки уведомления", e);
        // }
    }

    /**
     * Отправляет задачу с приоритетом
     */
    public void sendPriorityTask(String queueName, QueueMessage message, int priority) {
        log.warn("QueueService отключен - приоритетная задача не отправлена: queue={}, priority={}", queueName, priority);
        // try {
        //     message.setPriority(priority);
        //     message.setTimestamp(System.currentTimeMillis());

        //     String jsonMessage = objectMapper.writeValueAsString(message);
        //     
        //     rabbitTemplate.convertAndSend(queueName, jsonMessage, msg -> {
        //         msg.getMessageProperties().setPriority(priority);
        //         return msg;
        //     });
        //     
        //     log.info("Приоритетная задача отправлена в очередь {}: priority={}", queueName, priority);
        //     
        // } catch (Exception e) {
        //     log.error("Ошибка при отправке приоритетной задачи: {}", e.getMessage(), e);
        //     throw new RuntimeException("Ошибка отправки приоритетной задачи", e);
        // }
    }

    /**
     * Отправляет отложенную задачу
     */
    public void sendDelayedTask(String queueName, QueueMessage message, long delayMs) {
        log.warn("QueueService отключен - отложенная задача не отправлена: queue={}, delay={}ms", queueName, delayMs);
        // try {
        //     message.setTimestamp(System.currentTimeMillis());

        //     String jsonMessage = objectMapper.writeValueAsString(message);
        //     
        //     rabbitTemplate.convertAndSend(queueName, jsonMessage, msg -> {
        //         msg.getMessageProperties().setDelay((int) delayMs);
        //         return msg;
        //     });
        //     
        //     log.info("Отложенная задача отправлена в очередь {}: delay={}ms", queueName, delayMs);
        //     
        // } catch (Exception e) {
        //     log.error("Ошибка при отправке отложенной задачи: {}", e.getMessage(), e);
        //     throw new RuntimeException("Ошибка отправки отложенной задачи", e);
        // }
    }
}

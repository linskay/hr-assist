package com.example.hr_assistant.service.business;

import com.example.hr_assistant.model.Question;
import com.example.hr_assistant.model.dto.MediaUploadResponse;
import com.example.hr_assistant.repository.QuestionRepository;
import com.example.hr_assistant.service.external.AvatarClient;
import com.example.hr_assistant.service.external.TtsClient;
import com.example.hr_assistant.service.storage.MediaStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaGenerationService {

    private final TtsClient ttsClient;
    private final AvatarClient avatarClient;
    private final MediaStorageService mediaStorageService;
    private final QuestionRepository questionRepository;

    public String generateTtsForQuestion(Long questionId, String speaker, Double speed) {
        Question q = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        TtsClient.SynthesizeResponse resp = ttsClient.synthesize(q.getText(), speaker, speed);
        byte[] audio = ttsClient.download(resp.getPath());
        MediaUploadResponse up = mediaStorageService.uploadFile(
            audio,
            "q" + questionId + ".wav",
            "audio/wav",
            "tts"
        );
        if (Boolean.TRUE.equals(up.getSuccess())) {
            q.setTtsAudioUrl(up.getFileUrl());
            questionRepository.save(q);
        }
        return up.getFileUrl();
    }

    public String generateAvatarForQuestion(Long questionId, String faceImagePath) {
        Question q = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        if (q.getTtsAudioUrl() == null || q.getTtsAudioUrl().isEmpty()) {
            throw new IllegalStateException("TTS audio is required for avatar generation");
        }
        AvatarClient.GenerateResponse resp = avatarClient.generate(q.getTtsAudioUrl(), null, null, faceImagePath);
        byte[] video = avatarClient.download(resp.getPath());
        MediaUploadResponse up = mediaStorageService.uploadFile(
            video,
            "q" + questionId + ".mp4",
            "video/mp4",
            "avatar"
        );
        // Could store in separate field/model; here we reuse recordings or store url in question metadata if available
        return up.getFileUrl();
    }
}



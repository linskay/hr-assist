package com.example.hr_assistant.controller;

import com.example.hr_assistant.service.business.MediaGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media Generation")
public class MediaController {

    private final MediaGenerationService mediaGenerationService;

    @PostMapping("/tts/{questionId}")
    @Operation(summary = "Generate TTS for question")
    public ResponseEntity<TtsResponse> tts(@PathVariable Long questionId, @RequestBody TtsRequest req) {
        String url = mediaGenerationService.generateTtsForQuestion(questionId, req.getSpeaker(), req.getSpeed());
        TtsResponse resp = new TtsResponse();
        resp.setUrl(url);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/avatar/{questionId}")
    @Operation(summary = "Generate talking-head avatar for question using its TTS audio")
    public ResponseEntity<AvatarResponse> avatar(@PathVariable Long questionId, @RequestBody AvatarRequest req) {
        String url = mediaGenerationService.generateAvatarForQuestion(questionId, req.getFaceImagePath());
        AvatarResponse resp = new AvatarResponse();
        resp.setUrl(url);
        return ResponseEntity.ok(resp);
    }

    @Data
    public static class TtsRequest {
        private String speaker;
        private Double speed;
    }

    @Data
    public static class TtsResponse { private String url; }

    @Data
    public static class AvatarRequest { private String faceImagePath; }

    @Data
    public static class AvatarResponse { private String url; }
}



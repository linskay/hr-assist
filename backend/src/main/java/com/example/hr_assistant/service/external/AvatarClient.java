package com.example.hr_assistant.service.external;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class AvatarClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.avatar.base-url:http://avatar:8200}")
    private String baseUrl;

    public GenerateResponse generate(String audioUrl, String audioPath, String drivingAudioWav, String faceImagePath) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        GenerateRequest req = new GenerateRequest();
        req.setAudio_url(audioUrl);
        req.setAudio_path(audioPath);
        req.setDriving_audio_wav(drivingAudioWav);
        req.setFace_image_path(faceImagePath);
        return client.post()
            .uri("/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .retrieve()
            .bodyToMono(GenerateResponse.class)
            .doOnError(e -> log.error("Avatar generate error: {}", e.getMessage()))
            .block();
    }

    public byte[] download(String filename) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        ByteArrayResource res = client.get()
            .uri("/video/{filename}", filename)
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .retrieve()
            .bodyToMono(ByteArrayResource.class)
            .doOnError(e -> log.error("Avatar download error: {}", e.getMessage()))
            .block();
        return res != null ? res.getByteArray() : new byte[0];
    }

    @Data
    public static class GenerateRequest {
        private String audio_url;
        private String audio_path;
        private String driving_audio_wav;
        private String face_image_path;
    }

    @Data
    public static class GenerateResponse {
        private String path; // filename on service
    }
}



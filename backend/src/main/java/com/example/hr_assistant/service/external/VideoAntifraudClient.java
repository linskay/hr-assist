package com.example.hr_assistant.service.external;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoAntifraudClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.video-antifraud.base-url:http://localhost:8091}")
    private String baseUrl;

    public VerifyResponse verify(byte[] content, String filename, String contentType) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        mb.part("file", new ByteArrayResource(content) { @Override public String getFilename() { return filename; }})
            .header("Content-Type", contentType);
        return client.post().uri("/verify")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(mb.build()))
            .retrieve()
            .bodyToMono(VerifyResponse.class)
            .doOnError(e -> log.error("Ошибка /verify: {}", e.getMessage()))
            .block();
    }

    public LivenessResponse liveness(byte[] content, String filename, String contentType) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        mb.part("file", new ByteArrayResource(content) { @Override public String getFilename() { return filename; }})
            .header("Content-Type", contentType);
        return client.post().uri("/liveness")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(mb.build()))
            .retrieve()
            .bodyToMono(LivenessResponse.class)
            .doOnError(e -> log.error("Ошибка /liveness: {}", e.getMessage()))
            .block();
    }

    @Data
    public static class VerifyResponse { private boolean is_match; private double confidence; }
    @Data
    public static class LivenessResponse { private boolean is_live; private double confidence; }
}



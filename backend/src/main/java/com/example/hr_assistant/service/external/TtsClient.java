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
public class TtsClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.tts.base-url:http://tts:8100}")
    private String baseUrl;

    public SynthesizeResponse synthesize(String text, String speaker, Double speed) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        SynthesizeRequest req = new SynthesizeRequest();
        req.setText(text);
        req.setSpeaker(speaker);
        req.setSpeed(speed);
        return client.post()
            .uri("/synthesize")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .retrieve()
            .bodyToMono(SynthesizeResponse.class)
            .doOnError(e -> log.error("TTS synthesize error: {}", e.getMessage()))
            .block();
    }

    public byte[] download(String filename) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        ByteArrayResource res = client.get()
            .uri("/audio/{filename}", filename)
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .retrieve()
            .bodyToMono(ByteArrayResource.class)
            .doOnError(e -> log.error("TTS download error: {}", e.getMessage()))
            .block();
        return res != null ? res.getByteArray() : new byte[0];
    }

    @Data
    public static class SynthesizeRequest {
        private String text;
        private String speaker;
        private Double speed;
    }

    @Data
    public static class SynthesizeResponse {
        private String path; // filename on service
    }
}



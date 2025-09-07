package com.example.hr_assistant.service.external;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class DetectGptClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.detectgpt.base-url:http://localhost:8092}")
    private String baseUrl;

    public double detectAiProbability(String text) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        DetectRequest req = new DetectRequest();
        req.setText(text);
        DetectResponse resp = client.post()
            .uri("/detect")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .retrieve()
            .bodyToMono(DetectResponse.class)
            .doOnError(e -> log.error("Ошибка DetectGPT /detect: {}", e.getMessage()))
            .block();
        return resp != null ? resp.getAi_probability() : 0.0;
    }

    @Data
    public static class DetectRequest { private String text; }
    @Data
    public static class DetectResponse { private double ai_probability; }
}



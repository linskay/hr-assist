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
public class RubertClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.rubert.base-url:http://rubert:8093}")
    private String baseUrl;

    public ScoreResponse score(String answer, String requirement) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        ScoreRequest req = new ScoreRequest();
        req.setAnswer(answer);
        req.setRequirement(requirement);
        return client.post()
            .uri("/score")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .retrieve()
            .bodyToMono(ScoreResponse.class)
            .doOnError(e -> log.error("ruBERT /score error: {}", e.getMessage()))
            .block();
    }

    @Data
    public static class ScoreRequest { private String answer; private String requirement; }

    @Data
    public static class ScoreResponse { private double similarity; private double score; }
}



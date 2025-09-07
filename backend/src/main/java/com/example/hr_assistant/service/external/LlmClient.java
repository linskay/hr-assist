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
public class LlmClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.llm.base-url:http://localhost:8090}")
    private String baseUrl;

    public String generate(String prompt, int maxTokens, double temperature) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        GenerateRequest req = new GenerateRequest();
        req.setPrompt(truncate(prompt));
        req.setMax_tokens(maxTokens);
        req.setTemperature(temperature);
        GenerateResponse resp = client.post()
            .uri("/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .retrieve()
            .bodyToMono(GenerateResponse.class)
            .doOnError(e -> log.error("Ошибка вызова LLM /generate: {}", e.getMessage()))
            .block();
        return resp != null ? resp.getOutput() : "";
    }

    private String truncate(String s) {
        if (s == null) return "";
        int max = 4000;
        return s.length() > max ? s.substring(0, max) : s;
    }

    @Data
    public static class GenerateRequest {
        private String prompt;
        private Integer max_tokens;
        private Double temperature;
    }

    @Data
    public static class GenerateResponse {
        private String output;
    }
}



package com.example.hr_assistant.service.external;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmbedClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.sbert.base-url:http://localhost:8088}")
    private String baseUrl;

    public List<Double> embed(String text) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        EmbedRequest req = new EmbedRequest();
        req.setText(text);
        EmbedResponse resp = client.post()
                .uri("/embed")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(EmbedResponse.class)
                .onErrorResume(e -> {
                    log.error("Ошибка вызова /embed: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();
        return resp != null ? resp.getEmbedding() : List.of();
    }

    public double similarity(String a, String b) {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        SimilarityRequest req = new SimilarityRequest();
        req.setText1(a);
        req.setText2(b);
        SimilarityResponse resp = client.post()
                .uri("/similarity")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(SimilarityResponse.class)
                .onErrorResume(e -> {
                    log.error("Ошибка вызова /similarity: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();
        return resp != null ? resp.getSimilarity() : 0.0;
    }

    @Data
    public static class EmbedRequest { private String text; }
    @Data
    public static class EmbedResponse { private List<Double> embedding; }
    @Data
    public static class SimilarityRequest { private String text1; private String text2; }
    @Data
    public static class SimilarityResponse { private double similarity; }
}



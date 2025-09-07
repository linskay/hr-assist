package com.example.hr_assistant.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class WhisperXClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${whisperx.url:http://localhost:9000}")
    private String whisperxUrl;

    public String transcribe(
            File audioFile,
            String language,
            String modelSize,
            Integer batchSize,
            String computeType,
            Boolean diarize,
            Integer minSpeakers,
            Integer maxSpeakers
    ) {
        String url = whisperxUrl + "/transcribe";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));
        if (language != null) body.add("language", language);
        if (modelSize != null) body.add("model_size", modelSize);
        if (batchSize != null) body.add("batch_size", String.valueOf(batchSize));
        if (computeType != null) body.add("compute_type", computeType);
        if (diarize != null) body.add("diarize", String.valueOf(diarize));
        if (minSpeakers != null) body.add("min_speakers", String.valueOf(minSpeakers));
        if (maxSpeakers != null) body.add("max_speakers", String.valueOf(maxSpeakers));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return response.getBody();
    }
}



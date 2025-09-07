package com.example.hr_assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

    private String provider;
    private final Openai openai = new Openai();
    private final Gemini gemini = new Gemini();
    private final Llama llama = new Llama();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Openai getOpenai() {
        return openai;
    }

    public Gemini getGemini() {
        return gemini;
    }

    public Llama getLlama() {
        return llama;
    }

    public static class Openai {
        private String apiKey;
        private String model;

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    public static class Gemini {
        private String apiKey;
        private String model;

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    public static class Llama {
        private String apiUrl;
        private String model;

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }
}

package com.example.hr_assistant;

import com.example.hr_assistant.config.LlmProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableConfigurationProperties(LlmProperties.class)
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Backend!";
    }

    @Bean
    public CommandLineRunner commandLineRunner(LlmProperties properties) {
        return args -> {
            System.out.println("============================================================");
            System.out.println("Active LLM Provider: " + properties.getProvider());
            System.out.println("OpenAI Model: " + properties.getOpenai().getModel());
            System.out.println("============================================================");
        };
    }
}

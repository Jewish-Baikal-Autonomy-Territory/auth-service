package org.example.ai_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
public class AiAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAuthServiceApplication.class, args);
    }
}
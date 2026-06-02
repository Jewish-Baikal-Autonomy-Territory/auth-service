package org.example.ai_auth_service.messaging;

import org.example.ai_auth_service.event.EmailVerificationEvent;
import org.example.ai_auth_service.event.UserRegisteredEvent;
import org.example.ai_auth_service.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationConsumer {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "${TOPIC_EMAIL_VERIFICATION}", groupId = "auth-service-group")
    public void consumeEmailVerification(EmailVerificationEvent event) {
        emailService.sendVerificationEmail(event.getEmail(), event.getVerificationCode());
    }

}
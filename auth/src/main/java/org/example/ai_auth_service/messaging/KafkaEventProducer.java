package org.example.ai_auth_service.messaging;

import org.example.ai_auth_service.event.EmailVerificationEvent;
import org.example.ai_auth_service.event.PasswordChangedEvent;
import org.example.ai_auth_service.event.UserLoggedInEvent;
import org.example.ai_auth_service.event.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_EMAIL_VERIFICATION = "email-verification";
    private static final String TOPIC_USER_REGISTERED = "user-registered";
    private static final String TOPIC_USER_LOGIN = "user-login";
    private static final String TOPIC_PASSWORD_CHANGED = "password-changed";

    public void sendEmailVerificationEvent(EmailVerificationEvent event) {
        kafkaTemplate.send(TOPIC_EMAIL_VERIFICATION, event.getEmail(), event);
    }

    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        kafkaTemplate.send(TOPIC_USER_REGISTERED, event.getEmail(), event);
    }

    public void sendUserLoggedInEvent(UserLoggedInEvent event) {
        kafkaTemplate.send(TOPIC_USER_LOGIN, event.getEmail(), event);
    }

    public void sendPasswordChangedEvent(PasswordChangedEvent event) {
        kafkaTemplate.send(TOPIC_PASSWORD_CHANGED, event.getEmail(), event);
    }
}
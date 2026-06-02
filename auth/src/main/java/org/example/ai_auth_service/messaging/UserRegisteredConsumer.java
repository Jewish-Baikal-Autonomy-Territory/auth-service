package org.example.ai_auth_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.example.ai_auth_service.event.UserRegisteredEvent;
import org.example.ai_auth_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserRegisteredConsumer {

    @Autowired
    private UserService userService;

    @KafkaListener(topics = "${TOPIC_USER_REGISTERED}", groupId = "auth-service-group")
    public void consumeUserRegistered(UserRegisteredEvent event) {
        log.info("User registered: {}", event.getEmail());
    }
}

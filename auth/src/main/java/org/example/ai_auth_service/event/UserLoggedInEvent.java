package org.example.ai_auth_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoggedInEvent {
    private String email;
    private Instant loginTime;
}
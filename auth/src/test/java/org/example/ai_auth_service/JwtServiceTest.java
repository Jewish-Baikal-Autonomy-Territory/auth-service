package org.example.ai_auth_service;

import io.jsonwebtoken.Claims;
import org.example.ai_auth_service.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "EXPIRATION_TIME", 3600000L);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String phone = "+1234567890";
        String token = jwtService.generateToken(phone);
        assertThat(token).isNotEmpty();

        String extractedPhone = jwtService.extractUserName(token);
        assertThat(extractedPhone).isEqualTo(phone);
    }

    @Test
    void validateToken_WithCorrectUser_ShouldReturnTrue() {
        String phone = "+1234567890";
        String token = jwtService.generateToken(phone);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(phone);

        boolean isValid = jwtService.validateToken(token, userDetails);
        assertThat(isValid).isTrue();
    }
}
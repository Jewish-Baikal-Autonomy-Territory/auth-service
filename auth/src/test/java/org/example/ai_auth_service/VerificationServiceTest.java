package org.example.ai_auth_service;

import org.example.ai_auth_service.entity.UserVerification;
import org.example.ai_auth_service.repository.UserVerificationRepository;
import org.example.ai_auth_service.services.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock private UserVerificationRepository repository;
    @InjectMocks private VerificationService verificationService;

    @Test
    void verifyCode_WithValidCode_ShouldReturnTrue() {
        String email = "test@example.com";
        String code = "ABC123";
        UserVerification record = new UserVerification();
        record.setEmail(email);
        record.setVerificationCode(code);

        when(repository.findByEmail(email)).thenReturn(record);

        boolean result = verificationService.verifyCode(email, code);
        assertThat(result).isTrue();
    }

    @Test
    void verifyCode_WithWrongCode_ShouldReturnFalse() {
        String email = "test@example.com";
        when(repository.findByEmail(email)).thenReturn(null);

        boolean result = verificationService.verifyCode(email, "wrong");
        assertThat(result).isFalse();
    }
}
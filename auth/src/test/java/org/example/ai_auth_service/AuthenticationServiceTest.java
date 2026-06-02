//package org.example.ai_auth_service;
//
//import org.example.ai_auth_service.dto.JwtAuthenticationResponse;
//import org.example.ai_auth_service.dto.SignUpRequest;
//import org.example.ai_auth_service.entity.Role;
//import org.example.ai_auth_service.entity.User;
//import org.example.ai_auth_service.repository.JwtRepository;
//import org.example.ai_auth_service.repository.UserVerificationRepository;
//import org.example.ai_auth_service.services.*;
//import org.example.ai_auth_service.util.VerificationCodeGenerator;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthenticationServiceTest {
//
//    @Mock private UserService userService;
//    @Mock private JwtService jwtService;
//    @Mock private PasswordEncoder passwordEncoder;
//    @Mock private AuthenticationManager authenticationManager;
//    @Mock private JwtRepository jwtRepository;
//    @Mock private EmailService emailService;
//    @Mock private UserVerificationRepository verificationRepository;
//    @Mock private VerificationService verificationService;
//
//    @InjectMocks
//    private AuthenticationService authenticationService;
//
//    @Test
//    void signUp_ShouldCreateUserAndSendVerificationEmail() {
//        SignUpRequest request = new SignUpRequest();
//        request.setPhoneNumber("+1234567890");
//        request.setEmail("test@example.com");
//        request.setPassword("password123");
//        request.setFirstName("John");
//        request.setLastName("Doe");
//
//        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
//        when(userService.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
//        when(jwtService.generateToken("+1234567890")).thenReturn("jwt-token");
//
//        JwtAuthenticationResponse response = authenticationService.signUp(request);
//
//        assertThat(response.getToken()).isEqualTo("jwt-token");
//        verify(userService).create(argThat(user ->
//                user.getPhoneNumber().equals("+1234567890") &&
//                        user.getEmail().equals("test@example.com") &&
//                        user.getIsVerificated() == false &&
//                        user.getRole() == Role.USER
//        ));
//        verify(emailService).sendVerificationEmail(eq("test@example.com"), anyString());
//        verify(jwtRepository).save(any());
//    }
//
//    @Test
//    void signUp_WhenPhoneExists_ShouldThrowException() {
//        SignUpRequest request = new SignUpRequest();
//        request.setPhoneNumber("+1234567890");
//        request.setEmail("test@example.com");
//        request.setPassword("pass");
//        request.setFirstName("John");
//        request.setLastName("Doe");
//
//        when(userService.create(any(User.class))).thenThrow(new RuntimeException("User with this phone number already exists"));
//
//        assertThatThrownBy(() -> authenticationService.signUp(request))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("already exists");
//    }
//}
package org.example.ai_auth_service.services;

import lombok.RequiredArgsConstructor;
import org.example.ai_auth_service.dto.ChangePasswordRequest;
import org.example.ai_auth_service.dto.JwtAuthenticationResponse;
import org.example.ai_auth_service.dto.RefreshTokenRequest;
import org.example.ai_auth_service.dto.SignInRequest;
import org.example.ai_auth_service.dto.SignUpRequest;
import org.example.ai_auth_service.entity.JWT;
import org.example.ai_auth_service.entity.RefreshToken;
import org.example.ai_auth_service.entity.Role;
import org.example.ai_auth_service.entity.User;
import org.example.ai_auth_service.entity.UserVerification;
import org.example.ai_auth_service.event.EmailVerificationEvent;
import org.example.ai_auth_service.event.PasswordChangedEvent;
import org.example.ai_auth_service.event.UserLoggedInEvent;
import org.example.ai_auth_service.event.UserRegisteredEvent;
import org.example.ai_auth_service.messaging.KafkaEventProducer;
import org.example.ai_auth_service.repository.JwtRepository;
import org.example.ai_auth_service.repository.RefreshTokenRepository;
import org.example.ai_auth_service.repository.UserVerificationRepository;
import org.example.ai_auth_service.util.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtRepository jwtRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;
    private final UserVerificationRepository userVerificationRepository;
    private final VerificationService verificationService;
    private final KafkaEventProducer kafkaEventProducer;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        User user = new User();
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setIsVerificated(false);
        user.setRole(Role.USER);
        user.setCreatedAt(new Date());

        userService.create(user);
        UserRegisteredEvent registeredEvent = new UserRegisteredEvent(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber()
        );
        kafkaEventProducer.sendUserRegisteredEvent(registeredEvent);

        VerificationCodeGenerator codeGenerator = new VerificationCodeGenerator();
        String verificationCode = codeGenerator.generateCode();
        saveVerificationCode(user.getEmail(), verificationCode);

        EmailVerificationEvent event = new EmailVerificationEvent(user.getEmail(), verificationCode);
        kafkaEventProducer.sendEmailVerificationEvent(event);

        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = generateRefreshToken(user);
        saveJwtToken(accessToken, user);

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));


        User user = userService.getByUsername(request.getEmail());
        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = generateRefreshToken(user);
        UserLoggedInEvent loginEvent = new UserLoggedInEvent(
                user.getEmail(),
                Instant.now()
        );
        kafkaEventProducer.sendUserLoggedInEvent(loginEvent);
        saveJwtToken(accessToken, user);

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    @Transactional
    public JwtAuthenticationResponse refreshAccessToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthenticationServiceException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new AuthenticationServiceException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user.getUsername());

        saveJwtToken(newAccessToken, user);

        return new JwtAuthenticationResponse(newAccessToken, token);
    }

    public JwtAuthenticationResponse changePassword(ChangePasswordRequest request) {
        User user = userService.getByEmail(request.getToken().getUser().getEmail());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AuthenticationServiceException("Old password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmedNewPassword())) {
            throw new AuthenticationServiceException("New passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);

        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = generateRefreshToken(user);
        saveJwtToken(accessToken, user);
        PasswordChangedEvent pwdEvent = new PasswordChangedEvent(user.getEmail(), Instant.now());
        kafkaEventProducer.sendPasswordChangedEvent(pwdEvent);

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    public boolean verifyEmail(String email, String code) {
        boolean valid = verificationService.verifyCode(email, code);
        if (valid) {
            User user = userService.getByEmail(email);
            user.setIsVerificated(true);
            userService.save(user);
            return true;
        }
        return false;
    }

    private void saveJwtToken(String token, User user) {
        jwtRepository.findByUser(user).ifPresent(jwtRepository::delete);
        var jwtEntity = new JWT();
        jwtEntity.setToken(token);
        jwtEntity.setUser(user);
        jwtEntity.setCreatedAt(new Date());
        jwtRepository.save(jwtEntity);
    }

    private void saveVerificationCode(String email, String verificationCode) {
        UserVerification existing = userVerificationRepository.findByEmail(email);
        if (existing != null) {
            userVerificationRepository.delete(existing);
        }
        var verificationEntity = new UserVerification();
        verificationEntity.setEmail(email);
        verificationEntity.setVerificationCode(verificationCode);
        userVerificationRepository.save(verificationEntity);
    }

    private String generateRefreshToken(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMillis));
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
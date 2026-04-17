package org.example.ai_auth_service.services;

import lombok.RequiredArgsConstructor;
import org.example.ai_auth_service.dto.JwtAuthenticationResponse;
import org.example.ai_auth_service.dto.SignInRequest;
import org.example.ai_auth_service.dto.SignUpRequest;
import org.example.ai_auth_service.entity.JWT;
import org.example.ai_auth_service.entity.Role;
import org.example.ai_auth_service.entity.User;
import org.example.ai_auth_service.entity.UserVerification;
import org.example.ai_auth_service.repository.JwtRepository;
import org.example.ai_auth_service.repository.UserVerificationRepository;
import org.example.ai_auth_service.util.VerificationCodeGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtRepository jwtRepository;
    private final EmailService emailService;
    private final UserVerificationRepository userVerificationRepository;
    private final VerificationService verificationService;

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

        VerificationCodeGenerator codeGenerator = new VerificationCodeGenerator();
        String verificationCode = codeGenerator.generateCode();
        saveVerificationCode(user.getEmail(), verificationCode);
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);

        var jwt = jwtService.generateToken(user.getPhoneNumber());
        saveJwtToken(jwt, user);

        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getPhoneNumber(),
                request.getPassword()
        ));

        var user = userService.getByUsername(request.getPhoneNumber());
        var jwt = jwtService.generateToken(user.getUsername());
        saveJwtToken(jwt, user);

        return new JwtAuthenticationResponse(jwt);
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
}
package org.example.ai_auth_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ai_auth_service.dto.*;
import org.example.ai_auth_service.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody @Valid VerifyRequest request) {
        boolean verified = authenticationService.verifyEmail(request.getEmail(), request.getCode());
        if (verified) {
            return ResponseEntity.ok("Email verified successfully");
        }
        return ResponseEntity.badRequest().body("Invalid verification code");
    }

    @PostMapping("/change-passw")
    public JwtAuthenticationResponse changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return authenticationService.changePassword(request);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationResponse refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return authenticationService.refreshAccessToken(request);
    }
}
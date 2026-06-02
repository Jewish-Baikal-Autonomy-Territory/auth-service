package org.example.ai_auth_service.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.ai_auth_service.entity.JWT;
import org.example.ai_auth_service.entity.Role;
import org.example.ai_auth_service.entity.User;
import org.example.ai_auth_service.repository.JwtRepository;
import org.example.ai_auth_service.repository.RefreshTokenRepository;
import org.example.ai_auth_service.repository.UserRepository;
import org.example.ai_auth_service.services.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtRepository jwtRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName != null ? firstName : "");
            newUser.setLastName(lastName != null ? lastName : "");
            newUser.setPhoneNumber(null);
            newUser.setPassword(null);
            newUser.setIsVerificated(true);
            newUser.setRole(Role.USER);
            newUser.setCreatedAt(new Date());
            return userRepository.save(newUser);
        });
        user.setIsVerificated(true);
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = generateRefreshToken(user);
        saveJwtToken(accessToken, user);

//        String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + accessToken + "&refreshToken=" + refreshToken;
//        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private void saveJwtToken(String token, User user) {
        jwtRepository.findByUser(user).ifPresent(jwtRepository::delete);
        var jwtEntity = new JWT();
        jwtEntity.setToken(token);
        jwtEntity.setUser(user);
        jwtEntity.setCreatedAt(new Date());
        jwtRepository.save(jwtEntity);
    }

    private String generateRefreshToken(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        var refreshToken = new org.example.ai_auth_service.entity.RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(604800000));
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
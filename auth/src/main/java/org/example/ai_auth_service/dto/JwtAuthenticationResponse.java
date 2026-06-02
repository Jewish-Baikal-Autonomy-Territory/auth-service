package org.example.ai_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with JWT")
public class JwtAuthenticationResponse {

    @Schema(description = "JWT", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    private String token;

    @Schema(description = "Refresh token", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
}
package org.example.ai_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema
public class SignInRequest {

    @Schema(description = "Phone number", example = "+1234567890")
    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in valid international format")
    private String phoneNumber;



    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(min = 6, max = 60, message = "Password must be between 6 and 60 symbols")
    @NotBlank(message = "Password cannot be empty")
    private String password;


}

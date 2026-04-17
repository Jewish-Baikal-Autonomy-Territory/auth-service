package org.example.ai_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Register Request")
public class SignUpRequest {

    @Schema(description = "Email", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "Email must contain from 5 to 255 symbols")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be in format user@example.com")
    private String email;

    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(min = 6, max = 60, message = "Password must be between 6 and 60 symbols")
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Schema(description = "Phone number", example = "+1234567890")
    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in valid international format")
    private String phoneNumber;

    @Schema(description = "First name", example = "John")
    @NotBlank(message = "First name cannot be empty")
    @Size(max = 50, message = "First name must be up to 50 symbols")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 50, message = "Last name must be up to 50 symbols")
    private String lastName;

    @Schema(description = "Middle name", example = "Michael")
    @Size(max = 50, message = "Middle name must be up to 50 symbols")
    private String middleName;
}
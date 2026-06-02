package org.example.ai_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema
public class SignInRequest {

    @Schema(description = "Email", example = "bababoy123@gmail.com")
    @NotBlank(message = "Email cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email must be in valid international format")
    private String email;



    @Schema(description = "Password", example = "my_1secret1_password")
    @Size(min = 6, max = 60, message = "Password must be between 6 and 60 symbols")
    @NotBlank(message = "Password cannot be empty")
    private String password;


}

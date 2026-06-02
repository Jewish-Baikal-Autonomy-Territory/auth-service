package org.example.ai_auth_service.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.ai_auth_service.entity.JWT;

@Data
@Schema(description = "Change Password Request")
public class ChangePasswordRequest {

    @Schema(description = "JWT token")
    @NotBlank
    private JWT token;

    @Schema(description = "Old password", example = "Shishki123")
    @Size(min = 6, max = 60, message = "Password must be between 6 and 60 symbols")
    @NotBlank(message = "Field Old Password cannot be empty")
    private String oldPassword;

    @Schema(description = "New password", example = "DoraDura")
    @Size(min = 6, max = 60, message = "Password must be between 6 and 60 symbols")
    @NotBlank(message = "New password cannot be empty")
    private String newPassword;

    @Schema(description = "Confirmed new password", example = "DoraDura")
    @Size(min = 6, max = 60, message = "Password must be between 6 and 60 symbols")
    @NotBlank(message = "Field Confirm your new password cannot be empty")
    private String confirmedNewPassword;

}

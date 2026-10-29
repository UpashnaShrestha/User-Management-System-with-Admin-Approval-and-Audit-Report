package com.project.UserManagementSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ChangePasswordRequestDto {
    @NotBlank(message = "you should enter your current password.")
    private String currentPassword;
    @NotBlank
    @Size(min = 8, message = "New password must be at least 8 characters long.")
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}

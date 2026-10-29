package com.project.UserManagementSystem.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ForgotPasswordRequestDto {
    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email.")
    private String email;
}

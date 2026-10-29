package com.project.UserManagementSystem.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserRegisterRequestDto {
    @NotBlank(message = "Firstname is required.")
    private String firstName;

    @NotBlank(message = "Lastname is required.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must in valid format.")
    private String email;

    @NotBlank(message = "Username is required.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must contain at least 8 characters.")
    private String password;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be exactly 10 numeric digits."
    )
    private String phoneNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters.")
    private String address;

    @Past(message = "date of birth must be in past.")
    private LocalDate dateOfBirth;
}

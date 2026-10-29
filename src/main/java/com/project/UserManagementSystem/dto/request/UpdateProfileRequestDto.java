package com.project.UserManagementSystem.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateProfileRequestDto {
    @NotBlank(message = "Firstname is required.")
    private String firstName;

    @NotBlank(message = "lastname is required.")
    private String lastName;

    @Size(max = 255, message = "Address must not exceed 255 characters.")
    private String address;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be exactly 10 numeric digits."
    )
    private String phoneNumber;

    @Past(message = "date of birth must be in past.")
    private LocalDate dateOfBirth;
}

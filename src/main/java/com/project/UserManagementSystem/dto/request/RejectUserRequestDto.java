package com.project.UserManagementSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectUserRequestDto {
    @NotBlank(message = "Reason for rejection is required.")
    @Size(max = 200, message = "Reasonn cannot exceed 200 characters.")
    private String rejectReason;
}

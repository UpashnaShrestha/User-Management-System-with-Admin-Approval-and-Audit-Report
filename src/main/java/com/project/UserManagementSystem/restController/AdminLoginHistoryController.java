package com.project.UserManagementSystem.restController;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.resposne.LoginHistoryResponseDto;
import com.project.UserManagementSystem.service.LoginHistoryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/login-history")
public class AdminLoginHistoryController {
    private final LoginHistoryService loginHistoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LoginHistoryResponseDto>>>
    getAllLoginHistory(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String browser,
            @RequestParam(required = false) String os,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        if (fromDate != null && toDate != null
                && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "From date must be before or equal to the to date."
            );
        }

        if (status != null && !status.isBlank()
                && !status.equalsIgnoreCase("SUCCESS")
                && !status.equalsIgnoreCase("FAILED")) {
            throw new IllegalArgumentException(
                    "Status must be SUCCESS or FAILED."
            );
        }

        Page<LoginHistoryResponseDto> history =
                loginHistoryService.getAllLoginHistory(
                        username, browser, os, ipAddress, status,
                        fromDate, toDate, page, size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        history,
                        "Login history retrieved successfully."
                )
        );
    }
}

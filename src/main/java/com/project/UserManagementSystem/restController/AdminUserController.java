package com.project.UserManagementSystem.restController;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.request.RejectUserRequestDto;
import com.project.UserManagementSystem.dto.resposne.LoginHistoryResponseDto;
import com.project.UserManagementSystem.dto.resposne.PageResponseDto;
import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.service.AdminUserService;
import com.project.UserManagementSystem.service.CustomUserDetails;
import com.project.UserManagementSystem.service.LoginHistoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final LoginHistoryService loginHistoryService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getPendingUsers() {
        return ResponseEntity.ok(adminUserService.getPendingUsers());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<UserResponseDto>> approveUser(@PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User admin = userDetails.getUser();
        return ResponseEntity.ok(adminUserService.approveUsers(id, admin));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<UserResponseDto>> rejectUser(
            @PathVariable Integer id,
            @Valid @RequestBody RejectUserRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User admin = userDetails.getUser();
        return ResponseEntity.ok(adminUserService.rejectUsers(id, request.getRejectReason(), admin));
    }

    @PatchMapping("/{userId}/disable")
    public ResponseEntity<ApiResponse<UserResponseDto>> disableUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User admin = userDetails.getUser();
        adminUserService.disableUser(userId, admin);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/enable")
    public ResponseEntity<ApiResponse<UserResponseDto>> enableUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User admin = userDetails.getUser();
        adminUserService.enableUser(userId, admin );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/login-history")
    public ResponseEntity<ApiResponse<PageResponseDto<LoginHistoryResponseDto>>> getUserLoginHistory(
            @PathVariable Integer userId,
            @RequestParam(required = false) String browser,
            @RequestParam(required = false) String os,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        Page<LoginHistoryResponseDto> history = loginHistoryService.getUserLoginHistory(
                        userId, browser, os, ipAddress,
                        fromDate, toDate, page, size
                );
        PageResponseDto<LoginHistoryResponseDto> response = PageResponseDto.from(history);
        return ResponseEntity.ok(
                ApiResponse.success(response,"Login history retrieved successfully.")
        );
    }
}
package com.project.UserManagementSystem.service;

import com.project.UserManagementSystem.dto.resposne.LoginHistoryResponseDto;
import com.project.UserManagementSystem.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface LoginHistoryService {
    void recordSuccessfulLogin(User user, HttpServletRequest request);
    String getBrowser(String userAgent);
    Page<LoginHistoryResponseDto> getMyLoginHistory(String username, String browser, String os, String ipAddress, LocalDateTime fromDate, LocalDateTime toDate, int page, int size);

    Page<LoginHistoryResponseDto> getAllLoginHistory(String username, String browser, String os, String ipAddress, String status, LocalDateTime fromDate, LocalDateTime toDate, @Min(0) int page, @Min(1) @Max(100) int size);
//    Page<LoginHistoryResponseDto> getMyLoginHistory(String username, int page, int size);
    Page<LoginHistoryResponseDto> getUserLoginHistory(
            Integer userId,
            String browser,
            String os,
            String ipAddress,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size
    );
}

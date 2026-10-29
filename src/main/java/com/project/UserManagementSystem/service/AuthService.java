package com.project.UserManagementSystem.service;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.request.*;
import com.project.UserManagementSystem.dto.resposne.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    ApiResponse<LoginResponseDto> login(LoginRequestDto loginRequestDto, HttpServletRequest request);
    ApiResponse<Void> forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);
    ApiResponse<Void> resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
    ApiResponse<Void> logout(LogoutRequestDto requestDto);
    ApiResponse<LoginResponseDto> refreshToken(RefreshTokenRequestDto refreshTokenRequestDto);
}

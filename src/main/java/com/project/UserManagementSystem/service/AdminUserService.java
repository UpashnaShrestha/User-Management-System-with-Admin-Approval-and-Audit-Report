package com.project.UserManagementSystem.service;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.entity.User;

import java.util.List;

public interface AdminUserService {
    ApiResponse<List<UserResponseDto>> getPendingUsers();
    ApiResponse<UserResponseDto> approveUsers(Integer userId, User admin);
    ApiResponse<UserResponseDto> rejectUsers(Integer userId, String reason, User admin);
    ApiResponse<UserResponseDto> disableUser(Integer userId, User admin);
    ApiResponse<UserResponseDto> enableUser(Integer userId, User admin);
}

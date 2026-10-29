package com.project.UserManagementSystem.service;


import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.request.ChangePasswordRequestDto;
import com.project.UserManagementSystem.dto.request.UpdateProfileRequestDto;
import com.project.UserManagementSystem.dto.request.UserRegisterRequestDto;
import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.enums.RoleType;
import com.project.UserManagementSystem.enums.UserStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    ApiResponse<UserResponseDto> registerUser(UserRegisterRequestDto userRegisterRequestDto);
    ApiResponse<List<UserResponseDto>> getAllUsers();
    ApiResponse<UserResponseDto> getUserById(Integer id);
    ApiResponse<UserResponseDto> updateProfile(UpdateProfileRequestDto updateProfileRequestDto, String username);
    ApiResponse<Page<UserResponseDto>> searchUsers(
            String keyword,
            UserStatus status,
            RoleType role,
            int page,
            int size
    );
    ApiResponse<Void> changePassword(Integer userId, ChangePasswordRequestDto request);
    ApiResponse<UserResponseDto> getUserByUsername(String username);
}

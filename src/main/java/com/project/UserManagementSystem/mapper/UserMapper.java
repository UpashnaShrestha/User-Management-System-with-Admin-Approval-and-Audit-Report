package com.project.UserManagementSystem.mapper;

import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto toDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(user.getId());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setAddress(user.getAddress());
        userResponseDto.setDateOfBirth(user.getDateOfBirth());
        userResponseDto.setPhoneNumber(String.valueOf(user.getPhoneNumber()));
        userResponseDto.setRole(user.getRole());
        userResponseDto.setStatus(user.getStatus());
        userResponseDto.setCreatedAt(user.getCreatedAt());
        userResponseDto.setUpdatedAt(user.getUpdatedAt());
        userResponseDto.setRejectionReason(user.getRejectionReason());
        userResponseDto.setApprovedAt(user.getApprovedAt());

        return userResponseDto;
    }
}

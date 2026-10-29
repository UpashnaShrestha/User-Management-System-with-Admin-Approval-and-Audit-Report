package com.project.UserManagementSystem.service.impl;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.request.ChangePasswordRequestDto;
import com.project.UserManagementSystem.dto.request.UpdateProfileRequestDto;
import com.project.UserManagementSystem.dto.request.UserRegisterRequestDto;
import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.AuditAction;
import com.project.UserManagementSystem.enums.RoleType;
import com.project.UserManagementSystem.enums.UserStatus;
import com.project.UserManagementSystem.exceptions.EmailAlreadyExistsException;
import com.project.UserManagementSystem.exceptions.InvalidPasswordException;
import com.project.UserManagementSystem.exceptions.UserNotFoundException;
import com.project.UserManagementSystem.exceptions.UsernameAlreadyExistsException;
import com.project.UserManagementSystem.mapper.UserMapper;
import com.project.UserManagementSystem.repo.UserRepository;
import com.project.UserManagementSystem.service.EmailService;
import com.project.UserManagementSystem.service.UserAuditLogService;
import com.project.UserManagementSystem.service.UserService;
import com.project.UserManagementSystem.validation.PasswordStrengthValidator;
import com.project.UserManagementSystem.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordStrengthValidator passwordStrengthValidator;
    private final PasswordValidator passwordValidator;
    private final UserAuditLogService userAuditLogService;

    @Override
    public ApiResponse<UserResponseDto> registerUser(UserRegisterRequestDto userRegisterRequestDto) {
        if (userRepository.existsByUsername(userRegisterRequestDto.getUsername())) {
            throw new UsernameAlreadyExistsException("User already exists.");
        }
        if (userRepository.existsByEmail(userRegisterRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered.");
        }
        if(!userRegisterRequestDto.getPassword().equals(userRegisterRequestDto.getConfirmPassword())) {
            throw new InvalidPasswordException("Confirm password do not match the password.");
        }
        passwordStrengthValidator.validatePassword(userRegisterRequestDto.getPassword());
        User user = new User();

        user.setFirstName(userRegisterRequestDto.getFirstName().trim());
        user.setLastName(userRegisterRequestDto.getLastName().trim());
        user.setEmail(userRegisterRequestDto.getEmail().trim());
        user.setUsername(userRegisterRequestDto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));
        user.setPhoneNumber(userRegisterRequestDto.getPhoneNumber());
        user.setAddress(userRegisterRequestDto.getAddress());
        user.setDateOfBirth(userRegisterRequestDto.getDateOfBirth());
        user.setStatus(UserStatus.PENDING);
        user.setRole(RoleType.USER);
        User savedUser = userRepository.save(user);

        userAuditLogService.recordAction(
                savedUser,
                AuditAction.USER_REGISTERED,
                "User registered successfully.",
                savedUser
        );
        UserResponseDto userResponseDto = userMapper.toDto(savedUser);
        return ApiResponse.success(
                userResponseDto,
                "User registered pending. Waiting for admin approval."
        );
    }

    @Override
    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> response = users.stream()
                .map(userMapper::toDto)
                .toList();
        return ApiResponse.success(
                response,
                "Users retrieved successfully."
        );
    }

    @Override
    public ApiResponse<UserResponseDto> getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with id: "+id)
        );
        UserResponseDto userResponseDto = userMapper.toDto(user);
        return ApiResponse.success(
                userResponseDto,
                "User retrieved successfully."
        );
    }

    @Override
    public ApiResponse<UserResponseDto> getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found.")
        );
        return ApiResponse.success(userMapper.toDto(user), "Profile retrieved successfully.");
    }

    @Override
    public ApiResponse<UserResponseDto> updateProfile(UpdateProfileRequestDto updateProfileRequestDto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );
        user.setFirstName(updateProfileRequestDto.getFirstName().trim());
        user.setLastName(updateProfileRequestDto.getLastName().trim());
        user.setAddress(updateProfileRequestDto.getAddress());
        user.setDateOfBirth(updateProfileRequestDto.getDateOfBirth());

        User updatedUser = userRepository.save(user);
        UserResponseDto userResponseDto = userMapper.toDto(updatedUser);
        return ApiResponse.success(
                userResponseDto,
                "Profile updated successfully."
        );
    }

    @Override
    public ApiResponse<Page<UserResponseDto>> searchUsers(String keyword, UserStatus status, RoleType role, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative.");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100."
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<User> users = userRepository.searchUsers(
                keyword == null ? "" : keyword.trim(),
                status,
                role,
                pageable
        );

        return ApiResponse.success(
                users.map(userMapper::toDto),
                "Users retrieved successfully."
        );
    }

    @Override
    public ApiResponse<Void> changePassword(Integer userId, ChangePasswordRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidPasswordException(
                    "New password and confirmation do not match.");
        }

        passwordValidator.validatePassword(request.getNewPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
        userAuditLogService.recordAction(
                user,
                AuditAction.PASSWORD_CHANGED,
                "User changed their password.",
                user
        );
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Your Password Has Been Changed",
                    "Hello " + user.getFirstName() + ",\n\n"
                            + "Your password has been changed successfully.\n\n"
                            + "If you did not make this change, please contact "
                            + "the administrator immediately."
            );
        } catch (Exception e) {
            System.err.println(
                    "Failed to send password change notification email.");
        }

        return ApiResponse.success(
                null,
                "Password changed successfully.");
    }
}

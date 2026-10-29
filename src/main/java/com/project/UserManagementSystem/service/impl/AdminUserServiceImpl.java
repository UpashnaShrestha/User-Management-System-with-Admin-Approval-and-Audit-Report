package com.project.UserManagementSystem.service.impl;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.ApprovalAction;
import com.project.UserManagementSystem.enums.AuditAction;
import com.project.UserManagementSystem.enums.RoleType;
import com.project.UserManagementSystem.enums.UserStatus;
import com.project.UserManagementSystem.exceptions.UserNotFoundException;
import com.project.UserManagementSystem.mapper.UserMapper;
import com.project.UserManagementSystem.repo.UserRepository;
import com.project.UserManagementSystem.service.AdminUserService;
import com.project.UserManagementSystem.service.EmailService;
import com.project.UserManagementSystem.service.UserApprovalHistoryService;
import com.project.UserManagementSystem.service.UserAuditLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final UserApprovalHistoryService userApprovalHistoryService;
    private final UserAuditLogService userAuditLogService;

    @Override
    public ApiResponse<List<UserResponseDto>> getPendingUsers() {
        List<UserResponseDto> pendingUsers = userRepository.findByStatus(UserStatus.PENDING)
                .stream()
                .map(userMapper::toDto)
                .toList();
        return ApiResponse.success(
                pendingUsers,
                "Pending users retrieved successfully."
        );
    }

    @Override
    @Transactional
    public ApiResponse<UserResponseDto> approveUsers(Integer userId, User admin) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id: " + userId + " not found.")
        );
        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("Only pending users can be approved.");
        }
        user.setStatus(UserStatus.APPROVED);
        user.setApprovedAt(LocalDateTime.now());
        user.setRejectionReason(null);
        User savedUser = userRepository.save(user);

        userApprovalHistoryService.recordAction(user, ApprovalAction.APPROVED, "user account approved",admin);
        userAuditLogService.recordAction(
                savedUser,
                AuditAction.USER_APPROVED,
                "User account approved.",
                admin
        );

        emailService.sendEmail(
                savedUser.getEmail(),
                "Your Account Has Been Approved",
                "Hello " + savedUser.getFirstName() + ",\n\n"
                        + "Your account has been approved by the administrator. "
                        + "You can now log in to the User Management System.\n\n"
                        + "Thank you."
        );
        return ApiResponse.success(
                userMapper.toDto(savedUser),
                "User approved successfully."

        );
    }

    @Override
    @Transactional
    public ApiResponse<UserResponseDto> rejectUsers(Integer userId, String reason, User admin) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id: " + userId + " not found.")
        );
        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("Only pending users can be rejected.");
        }
        if(reason == null || reason.isBlank()){
            throw new IllegalStateException("Reason for rejection is required.");
        }
        user.setStatus(UserStatus.REJECTED);
        user.setRejectionReason(reason.trim());
        user.setApprovedAt(null);

        User savedUser = userRepository.save(user);
        userApprovalHistoryService.recordAction(user, ApprovalAction.REJECTED, "user account rejected",admin);
        userAuditLogService.recordAction(
                savedUser,
                AuditAction.USER_REJECTED,
                "User account rejected.",
                admin
        );

        emailService.sendEmail(
                savedUser.getEmail(),
                "Your Account Registration Was Rejected",
                "Hello " + savedUser.getFirstName() + ",\n\n"
                        + "Unfortunately, your registration has been rejected "
                        + "by the administrator.\n\n"
                        + "Reason: " + savedUser.getRejectionReason() + "\n\n"
                        + "If you have questions, please contact the administrator."
        );

        return ApiResponse.success(
                userMapper.toDto(savedUser),
                "User rejected successfully."
        );
    }

    @Override
    public ApiResponse<UserResponseDto> disableUser(Integer userId, User admin){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        if (user.getRole() == RoleType.ADMIN) {
            throw new IllegalArgumentException(
                    "Admin accounts cannot be disabled through this operation."
            );
        }

        user.setStatus(UserStatus.DISABLED);
        User savedUser = userRepository.save(user);
        userApprovalHistoryService.recordAction(user, ApprovalAction.DISABLED, "user account disabled",admin);
        userAuditLogService.recordAction(
                savedUser,
                AuditAction.USER_DISABLED,
                "User account disabled.",
                admin
        );


        try {
            emailService.sendEmail(
                    savedUser.getEmail(),
                    "Your Account Has Been Disabled",
                    "Hello " + savedUser.getFirstName() + ",\n\n"
                            + "Your account has been disabled by the administrator.\n"
                            + "You will not be able to log in while your account "
                            + "is disabled.\n\n"
                            + "If you believe this was a mistake, please contact "
                            + "the administrator."
            );
        } catch (Exception e) {System.err.println("Failed to send account disabled notification email.");
        }
        UserResponseDto responseDto = userMapper.toDto(savedUser);

        return ApiResponse.success(
                responseDto,
                "User account disabled successfully."
        );
    }

    @Override
    public ApiResponse<UserResponseDto> enableUser(Integer userId, User admin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        if (user.getRole() == RoleType.ADMIN) {
            throw new IllegalArgumentException(
                    "Admin accounts cannot be enabled through this operation."
            );
        }

        if (user.getStatus() != UserStatus.DISABLED) {
            throw new IllegalArgumentException(
                    "Only disabled users can be enabled."
            );
        }

        user.setStatus(UserStatus.APPROVED);
        User savedUser = userRepository.save(user);
        userApprovalHistoryService.recordAction(user, ApprovalAction.ENABLED, "user account enabled",admin);
        userAuditLogService.recordAction(
                savedUser,
                AuditAction.USER_ENABLED,
                "User account approved.",
                admin
        );

        try {
            emailService.sendEmail(
                    savedUser.getEmail(),
                    "Your Account Has Been Re-enabled",
                    "Hello " + savedUser.getFirstName() + ",\n\n"
                            + "Your account has been re-enabled by the administrator.\n"
                            + "You can log in again if your account is approved.\n\n"
                            + "If you have any questions, please contact "
                            + "the administrator."
            );
        } catch (Exception e) {
            System.err.println(
                    "Failed to send account re-enabled notification email.");
        }
        UserResponseDto responseDto = userMapper.toDto(savedUser);

        return ApiResponse.success(
                responseDto,
                "User account enabled successfully."
        );
    }
}

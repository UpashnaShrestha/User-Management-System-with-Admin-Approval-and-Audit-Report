package com.project.UserManagementSystem.service.impl;


import com.project.UserManagementSystem.dto.resposne.AdminDashboardResponseDto;
import com.project.UserManagementSystem.enums.LoginStatus;
import com.project.UserManagementSystem.enums.UserStatus;
import com.project.UserManagementSystem.repo.LoginHistoryRepository;
import com.project.UserManagementSystem.repo.UserRepository;
import com.project.UserManagementSystem.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public AdminDashboardResponseDto getDashboardStatistics() {
        long totalUsers = userRepository.count();
        long pendingUsers = userRepository.countByStatus(UserStatus.PENDING);
        long approvedUsers = userRepository.countByStatus(UserStatus.APPROVED);
        long rejectedUsers = userRepository.countByStatus(UserStatus.REJECTED);
        long disabledUsers = userRepository.countByStatus(UserStatus.DISABLED);
        long successfulLogins = loginHistoryRepository.countByLoginStatus(LoginStatus.SUCCESS);
        long failedLogins = loginHistoryRepository.countByLoginStatus(LoginStatus.FAILED);
        long totalLoginAttempts = successfulLogins + failedLogins;
        return new AdminDashboardResponseDto(
                totalUsers,
                pendingUsers,
                approvedUsers,
                rejectedUsers,
                disabledUsers,
                totalLoginAttempts,
                successfulLogins,
                failedLogins
        );
    }
}
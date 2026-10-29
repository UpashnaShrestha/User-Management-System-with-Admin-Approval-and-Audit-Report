package com.project.UserManagementSystem.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AdminDashboardResponseDto {
    private long totalUsers;
    private long pendingUsers;
    private long approvedUsers;
    private long rejectedUsers;
    private long disabledUsers;
    private long totalLoginAttempts;
    private long successfulLogins;
    private long failedLogins;
}

package com.project.UserManagementSystem.service;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.entity.UserAuditLog;
import com.project.UserManagementSystem.enums.AuditAction;
import com.project.UserManagementSystem.repo.UserAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuditLogService {
    private final UserAuditLogRepository userAuditLogRepository;
    public void recordAction(
            User user,
            AuditAction action,
            String description,
            User performedBy
    ){
        UserAuditLog auditLog = new UserAuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setDescription(description);
        auditLog.setPerformedBy(performedBy);
        userAuditLogRepository.save(auditLog);

        userAuditLogRepository.save(auditLog);
    }
}

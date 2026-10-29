package com.project.UserManagementSystem.service;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.entity.UserApprovalHistory;
import com.project.UserManagementSystem.enums.ApprovalAction;
import com.project.UserManagementSystem.repo.UserApprovalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserApprovalHistoryService {
    private final UserApprovalHistoryRepository userApprovalHistoryRepository;
    public void recordAction(
            User user,
            ApprovalAction action,
            String reason,
            User admin
    ){
        UserApprovalHistory userApprovalHistory = new UserApprovalHistory();
        userApprovalHistory.setUser(user);
        userApprovalHistory.setAction(action);
        userApprovalHistory.setReason(reason);
        userApprovalHistory.setPerformedBy(admin);
        userApprovalHistoryRepository.save(userApprovalHistory);
    }


}

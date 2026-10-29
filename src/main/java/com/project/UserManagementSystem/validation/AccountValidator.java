package com.project.UserManagementSystem.validation;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.UserStatus;
import com.project.UserManagementSystem.exceptions.AccountNotApprovedException;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {
    public void validateAccountStatus(User user) {
        if (user.getStatus() == UserStatus.PENDING) {
            throw new AccountNotApprovedException("Your account is waiting for admin approval.");
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            throw new AccountNotApprovedException("Your account is rejected. Reason: " + user.getRejectionReason());
        }

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new AccountNotApprovedException("Your account is disabled. Please contact your administrator.");
        }
    }
}

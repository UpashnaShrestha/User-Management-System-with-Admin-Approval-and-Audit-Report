package com.project.UserManagementSystem.service;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class LoginAttemptService {
    private final UserRepository userRepository;
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 10 ;

    public void loginFailed(User user){
         user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
         if(user.getFailedLoginAttempts() >= MAX_ATTEMPTS) {
             user.setLockTime(LocalDateTime.now());
         }
         userRepository.save(user);
    }

    public void loginSucceeded(User user){
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    public boolean isLocked(User user){
        if (user.getLockTime() == null) {
            return false;
        }
        LocalDateTime unlockTime = user.getLockTime().plusMinutes(LOCK_DURATION_MINUTES);
        if(LocalDateTime.now().isBefore(unlockTime)) {
            return true;
        }
        loginSucceeded(user);
        return false;
    }
}

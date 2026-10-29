package com.project.UserManagementSystem.validation;

import com.project.UserManagementSystem.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator{
    public void validatePassword(String password){
        if (password == null || password.length() < 8
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[^a-zA-Z0-9].*")) {

            throw new InvalidPasswordException("Password must contain at least 8 characters, "
                    + "one uppercase letter, one lowercase letter, "
                    + "one digit, and one special character.");
        }
    }
}

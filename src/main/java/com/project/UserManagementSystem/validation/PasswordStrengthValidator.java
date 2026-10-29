package com.project.UserManagementSystem.validation;

import com.project.UserManagementSystem.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Component;

@Component
public class PasswordStrengthValidator {
    public void validatePassword(String password){
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException(
                    "Password cannot be blank."
            );
        }

        if (password.length() < 8) {
            throw new InvalidPasswordException(
                    "Password must be at least 8 characters long."
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidPasswordException(
                    "Password must contain at least one capital letter."
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new InvalidPasswordException(
                    "Password must contain at least one lowercase letter."
            );
        }

        if (!password.matches(".*[0-9].*")) {
            throw new InvalidPasswordException(
                    "Password must contain at least one digit."
            );
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            throw new InvalidPasswordException(
                    "Password must contain at least one special character (@#$%^&+=!)."
            );
        }
    }
}

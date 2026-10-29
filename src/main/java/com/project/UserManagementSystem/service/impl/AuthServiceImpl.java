package com.project.UserManagementSystem.service.impl;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.request.*;
import com.project.UserManagementSystem.dto.resposne.LoginResponseDto;
import com.project.UserManagementSystem.entity.PasswordResetToken;
import com.project.UserManagementSystem.entity.RefreshToken;
import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.AuditAction;
import com.project.UserManagementSystem.exceptions.InvalidPasswordException;
import com.project.UserManagementSystem.exceptions.UserNotFoundException;
import com.project.UserManagementSystem.mapper.UserMapper;
import com.project.UserManagementSystem.repo.PasswordResetTokenRepository;
import com.project.UserManagementSystem.repo.RefreshTokenRepository;
import com.project.UserManagementSystem.repo.UserRepository;
import com.project.UserManagementSystem.service.*;
import com.project.UserManagementSystem.validation.AccountValidator;
import com.project.UserManagementSystem.validation.PasswordValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginHistoryService loginHistoryService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountValidator accountValidator;
    private final PasswordValidator  passwordValidator;
    private final CustomUserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;
    private final UserAuditLogService userAuditLogService;

    @Override
    public ApiResponse<LoginResponseDto> login(LoginRequestDto loginRequestDto, HttpServletRequest request) { //httpServlet device , network,etc
        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new UserNotFoundException("Invalid username or password.")
        );
        userAuditLogService.recordAction(
                user,
                AuditAction.USER_LOGIN,
                "User logged in successfully.",
                user
        );
        accountValidator.validateAccountStatus(user);
        if (loginAttemptService.isLocked(user)) {
            throw new BadCredentialsException("Account is temporarily locked. Please try again later.");
        }
        
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
            );
            loginAttemptService.loginSucceeded(user);
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(user);
            throw new BadCredentialsException("Invalid username or password.");
        }
        
        //to generate jwt token : gets username
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        //records successful login after verification
        loginHistoryService.recordSuccessfulLogin(user, request);
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "New Login to Your Account",
                    "Hello " + user.getFirstName() + ",\n\n"
                            + "A successful login to your account was detected.\n"
                            + "Time: " + LocalDateTime.now() + "\n\n"
                            + "If this wasn't you, please change your password "
                            + "and contact the administrator."
            );
        } catch (Exception e) {
            System.err.println("Failed to send login notification email.");
        }

        String accessToken = jwtService.generateAccessToken(userDetails, new HashMap<>());
        String refreshToken = jwtService.generateRefreshToken(userDetails, new HashMap<>());

        RefreshToken storedToken = new RefreshToken();
        storedToken.setTokenHash(jwtService.hashToken(refreshToken));
        storedToken.setUser(user);
        storedToken.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtService.getRefreshTokenExpiration())));
        storedToken.setRevoked(false);
        refreshTokenRepository.save(storedToken);

        LoginResponseDto response = new LoginResponseDto(
                accessToken,
                refreshToken,
                userMapper.toDto(user)
        );

        return ApiResponse.success(
                response,
                "User logged in successfully."
        );
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Could not hash token.", e);
        }
    }

    @Override
    public ApiResponse<Void> forgotPassword(ForgotPasswordRequestDto request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            passwordResetTokenRepository.findAll()
                    .stream()
                    .filter(t -> t.getUser().getId().equals(user.getId()) && !t.getUsed())
                    .forEach(t -> {
                        t.setUsed(true);
                        passwordResetTokenRepository.save(t);
                    });

            String token = generateResetToken();

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setTokenHash(hashToken(token));
            resetToken.setUser(user);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            resetToken.setUsed(false);
            passwordResetTokenRepository.save(resetToken);

            String resetLink =
                    "http://localhost:5173/reset-password?token=" + token;

            emailService.sendEmail(
                    user.getEmail(),
                    "Password Reset Request",
                    "Use this link to reset your password:\n"
                            + resetLink
                            + "\n\nThis link expires in 15 minutes."
            );
        });

        return ApiResponse.success(
                null,
                "If that email is registered, a reset link will be sent.");
    }

    @Override
    public ApiResponse<Void> resetPassword(ResetPasswordRequestDto request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirmation do not match.");
        }
        passwordValidator.validatePassword(request.getNewPassword());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(hashToken(request.getToken()))
                        .orElseThrow(() -> new InvalidPasswordException("Invalid or expired reset token."));

        if (resetToken.getUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidPasswordException("Invalid or expired reset token.");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Send notification after the password reset is completed.
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Your Password Has Been Reset",
                    "Hello " + user.getFirstName() + ",\n\n"
                            + "Your password was reset successfully.\n\n"
                            + "If you did not request this reset, please contact "
                            + "the administrator immediately."
            );
        } catch (Exception e) {
            // Log the email failure; do not undo the password reset.
            System.err.println("Failed to send password reset notification.");
        }

        return ApiResponse.success(null, "Password reset successfully.");
    }

    @Override
    @Transactional
    public ApiResponse<Void> logout(LogoutRequestDto requestDto) {

        String tokenHash = jwtService.hashToken(requestDto.getRefreshToken());
        RefreshToken storedToken = refreshTokenRepository.findByTokenHashForUpdate(tokenHash).orElseThrow(
                () -> new IllegalStateException("Invalid refresh token")
        );

        if (storedToken.isRevoked()) {
            return ApiResponse.success(null, "Token is already revoked.");
        }
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ApiResponse.success(null, "Refresh token has expired.");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();
        userAuditLogService.recordAction(
                user,
                AuditAction.USER_LOGOUT,
                "User logged out successfully.",
                user
        );
        return ApiResponse.success(null, "Logged out successfully.");
    }

    @Override
    @Transactional
    public ApiResponse<LoginResponseDto> refreshToken(RefreshTokenRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh token has expired.");
        }

        if (!"refresh_token".equals(jwtService.extractTokenType(refreshToken))) {
            throw new IllegalArgumentException("Invalid token type.");
        }

        String tokenHash = jwtService.hashToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token has expired or been revoked.");
        }

        User user = storedToken.getUser();
        accountValidator.validateAccountStatus(user);

//this is for access token refresh only -> WITHOUT ROTATION
//        // generate a new access token
//        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
//
//        String newAccessToken = jwtService.generateAccessToken(
//                userDetails, new HashMap<>());
//
//        // Return the new access token and existing refresh token
//        LoginResponseDto response = new LoginResponseDto(
//                newAccessToken,
//                refreshToken,
//                userMapper.toDto(user));
//
//        return ApiResponse.success(response, "Access token refreshed successfully.");

        // Load the user's security details
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtService.generateAccessToken(userDetails, new HashMap<>());
        String newRefreshToken = jwtService.generateRefreshToken(userDetails, new HashMap<>());

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        //new refresh token hash storing
        RefreshToken newStoredToken = new RefreshToken();
        newStoredToken.setTokenHash(jwtService.hashToken(newRefreshToken));
        newStoredToken.setUser(user);
        newStoredToken.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtService.getRefreshTokenExpiration())));
        newStoredToken.setRevoked(false);

        refreshTokenRepository.save(newStoredToken);

        LoginResponseDto response = new LoginResponseDto(
                newAccessToken,
                newRefreshToken,
                userMapper.toDto(user)
        );

        return ApiResponse.success(
                response,
                "Tokens refreshed successfully."
        );

    }
}

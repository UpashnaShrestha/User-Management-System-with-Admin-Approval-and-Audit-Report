package com.project.UserManagementSystem.restController;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.request.*;
import com.project.UserManagementSystem.dto.resposne.LoginResponseDto;
import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.exceptions.UserNotFoundException;
import com.project.UserManagementSystem.repo.UserRepository;
import com.project.UserManagementSystem.service.AuthService;
import com.project.UserManagementSystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@Valid @RequestBody UserRegisterRequestDto userRegisterRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(userRegisterRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(loginRequestDto, request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequestDto request, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new UserNotFoundException("User not found."));
        return ResponseEntity.ok(userService.changePassword(user.getId(), request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        return ResponseEntity.ok(authService.forgotPassword(forgotPasswordRequestDto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>>  resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequestDto requestDto) {
        return ResponseEntity.ok(authService.logout(requestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequestDto));
    }
}

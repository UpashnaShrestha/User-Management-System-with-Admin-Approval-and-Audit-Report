package com.project.UserManagementSystem.restController;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.request.UpdateProfileRequestDto;
import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyProfile(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        ApiResponse<UserResponseDto> response = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateProfile(@Valid @RequestBody UpdateProfileRequestDto updateProfileRequestDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        ApiResponse<UserResponseDto> response = userService.updateProfile( updateProfileRequestDto, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

}

package com.project.UserManagementSystem.restController;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.resposne.UserResponseDto;
import com.project.UserManagementSystem.enums.RoleType;
import com.project.UserManagementSystem.enums.UserStatus;
import com.project.UserManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) RoleType role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size
    ){
        return ResponseEntity.ok(userService.searchUsers(keyword, status, role, page, size));
    }
}
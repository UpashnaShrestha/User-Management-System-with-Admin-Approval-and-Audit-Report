package com.project.UserManagementSystem.restController;

import com.project.UserManagementSystem.dto.resposne.AdminDashboardResponseDto;
import com.project.UserManagementSystem.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    private final AdminDashboardService dashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<AdminDashboardResponseDto> getDashboardStatistics() {
        return ResponseEntity.ok(dashboardService.getDashboardStatistics());
    }
}
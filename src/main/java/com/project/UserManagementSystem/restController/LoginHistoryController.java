package com.project.UserManagementSystem.restController;

import com.project.UserManagementSystem.dto.ApiResponse;
import com.project.UserManagementSystem.dto.resposne.LoginHistoryResponseDto;
import com.project.UserManagementSystem.dto.resposne.PageResponseDto;
import com.project.UserManagementSystem.service.LoginHistoryReportService;
import com.project.UserManagementSystem.service.LoginHistoryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/login-history")
public class LoginHistoryController {
    private final LoginHistoryService loginHistoryService;
    private final LoginHistoryReportService loginHistoryReportService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<LoginHistoryResponseDto>>> getMyLoginHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue ="10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String browser,
            @RequestParam(required = false) String os,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate){

            if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
                throw new IllegalArgumentException(
                        "From date must be before or equal to the to date."
                );
            }
            Page<LoginHistoryResponseDto> history = loginHistoryService.getMyLoginHistory(
                    userDetails.getUsername(),
                    browser,
                    os,
                    ipAddress,
                    fromDate,
                    toDate,
                    page,
                    size
            );
            PageResponseDto<LoginHistoryResponseDto> response = PageResponseDto.from(history);

            return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Login history retrieved successfully."
                )
            );
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<byte[]> downloadMyPdfRepost(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        byte[] pdf = loginHistoryReportService.
                generatePdfReport(userDetails.getUsername(),null, null, null, null, null, null);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"my-login-history-report.pdf\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/report/word")
    public ResponseEntity<byte[]> downloadMyWordRepost(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        byte[] word = loginHistoryReportService.generateWordReport(userDetails.getUsername(),null, null, null, null, null, null);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"my-login-history-report.docx\""
                )
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(word);
    }

    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> downloadMyExcelRepost(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        byte[] excel = loginHistoryReportService.generateExcelReport(userDetails.getUsername(),null, null, null, null, null, null);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"my-login-history-report.xlsx\""
                )
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}

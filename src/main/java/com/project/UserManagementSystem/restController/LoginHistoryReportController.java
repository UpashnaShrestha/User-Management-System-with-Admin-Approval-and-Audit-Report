package com.project.UserManagementSystem.restController;

import com.project.UserManagementSystem.service.LoginHistoryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/login-history/report")
public class LoginHistoryReportController {
    private final LoginHistoryReportService reportService;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String browser,
            @RequestParam(required = false) String os,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate
    ) throws IOException {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From date must be before or equal to to date.");
        }

        byte[] pdf = reportService.generatePdfReport(username, browser, os, ipAddress, status, fromDate, toDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"login-history-report.pdf\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @GetMapping("/word")
    public ResponseEntity<byte[]> downloadWordReport(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String browser,
            @RequestParam(required = false) String os,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate
    ) throws IOException {

        validateDateRange(fromDate, toDate);

        byte[] wordBytes = reportService.generateWordReport(
                username, browser, os, ipAddress, status, fromDate, toDate
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"login-history-report.docx\""
                )
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ))
                .body(wordBytes);
    }

    private void validateDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate != null && toDate != null
                && fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'fromDate' must be before or equal to 'toDate'."
            );
        }
    }


    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadExcelReport(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String browser,
            @RequestParam(required = false) String os,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate
    ) throws IOException {

        validateDateRange(fromDate, toDate);

        byte[] excelBytes = reportService.generateExcelReport(
                username, browser, os, ipAddress, status, fromDate, toDate
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"login-history-report.xlsx\""
                )
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(excelBytes);
    }
}

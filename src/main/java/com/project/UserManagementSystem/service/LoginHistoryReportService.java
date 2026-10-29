package com.project.UserManagementSystem.service;


import java.io.IOException;
import java.time.LocalDateTime;

public interface LoginHistoryReportService {
    byte[] generatePdfReport(String username, String os, String browser, String ipAddress, String status, LocalDateTime fromDate, LocalDateTime toDate)throws IOException;
    byte[] generateWordReport(String username, String browser, String os, String ipAddress, String status, LocalDateTime fromDate, LocalDateTime toDate) throws IOException;
    byte[] generateExcelReport(String username, String browser, String os, String ipAddress, String status, LocalDateTime fromDate, LocalDateTime toDate) throws IOException;
}

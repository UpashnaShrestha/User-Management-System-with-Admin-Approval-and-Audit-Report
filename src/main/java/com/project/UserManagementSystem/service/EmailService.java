package com.project.UserManagementSystem.service;


public interface EmailService {
    void sendEmail(String to, String subject, String body);
}

package com.project.UserManagementSystem.dto.resposne;


import com.project.UserManagementSystem.enums.LoginStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LoginHistoryResponseDto {
    private Integer id;
    private LocalDateTime loginTime;
    private String ipAddress;
    private String browser;
    private String operatingSystem;
    private String device;
    private LoginStatus loginStatus;

}

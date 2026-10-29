package com.project.UserManagementSystem.entity;

import com.project.UserManagementSystem.enums.LoginStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "login_time",nullable = false, updatable = false)
    private LocalDateTime loginTime;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(length = 100)
    private String device;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_status", length = 20, nullable = false)
    private LoginStatus loginStatus;

}

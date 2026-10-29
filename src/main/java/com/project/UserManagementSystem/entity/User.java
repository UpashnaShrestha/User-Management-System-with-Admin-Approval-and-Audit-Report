package com.project.UserManagementSystem.entity;

import com.project.UserManagementSystem.enums.RoleType;
import com.project.UserManagementSystem.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username",columnNames = "username"),
            @UniqueConstraint(name = "uk_users_email",columnNames = "email")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 50, name = "phone_number")
    private String phoneNumber;

    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    private Integer failedLoginAttempts = 0;
    private LocalDateTime lockTime;

//    @PrePersist
//    public void prePersist() {
//        this.createdAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    public void preUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
}

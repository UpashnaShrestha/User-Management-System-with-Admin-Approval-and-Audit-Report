package com.project.UserManagementSystem.entity;

import com.project.UserManagementSystem.enums.ApprovalAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_approval_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserApprovalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalAction action;

    @Column(length = 100)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    @PrePersist
    public void onCreate() {
        if (performedAt== null) {
            performedAt = LocalDateTime.now();
        }
    }
}

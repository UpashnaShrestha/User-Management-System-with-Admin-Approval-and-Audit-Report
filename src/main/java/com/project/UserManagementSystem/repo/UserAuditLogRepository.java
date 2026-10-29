package com.project.UserManagementSystem.repo;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.entity.UserAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuditLogRepository extends JpaRepository<UserAuditLog,Integer> {
    Page<UserAuditLog> findByUserOrderByPerformedAtDesc(User user, Pageable pageable);
}

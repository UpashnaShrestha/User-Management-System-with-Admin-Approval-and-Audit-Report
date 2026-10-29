package com.project.UserManagementSystem.repo;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.entity.UserApprovalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserApprovalHistoryRepository extends JpaRepository<UserApprovalHistory, Integer> {
    Page<UserApprovalHistory> findByUserOrderByPerformedAtDesc(User user, Pageable pageable);
}

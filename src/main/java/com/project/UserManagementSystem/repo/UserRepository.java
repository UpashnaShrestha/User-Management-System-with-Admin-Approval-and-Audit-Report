package com.project.UserManagementSystem.repo;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.RoleType;
import com.project.UserManagementSystem.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByStatus(UserStatus status);
    long countByStatus(UserStatus status);

    @Query("""
    SELECT u FROM User u
    WHERE (:keyword = '' 
        OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:status IS NULL OR u.status = :status)
      AND (:role IS NULL OR u.role = :role)
    ORDER BY u.createdAt DESC
    """)
    Page<User> searchUsers(
            @Param("keyword") String keyword,
            @Param("status") UserStatus status,
            @Param("role") RoleType role,
            Pageable pageable
    );
}

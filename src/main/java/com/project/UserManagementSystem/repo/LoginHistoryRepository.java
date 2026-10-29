package com.project.UserManagementSystem.repo;


import com.project.UserManagementSystem.entity.LoginHistory;
import com.project.UserManagementSystem.enums.LoginStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {
    Page<LoginHistory> findByUserIdOrderByLoginTimeDesc(Integer userId, Pageable pageable);
    long countByLoginStatus(LoginStatus loginStatus);

    @Query("""
    SELECT h FROM LoginHistory h
    WHERE h.user.id = :userId
      AND (:browser = '' OR
           LOWER(h.browser) LIKE LOWER(CONCAT('%', :browser, '%')))
      AND (:os = '' OR
           LOWER(h.operatingSystem) LIKE LOWER(CONCAT('%', :os, '%')))
      AND (:ipAddress = '' OR
           h.ipAddress LIKE CONCAT('%', :ipAddress, '%'))
      AND h.loginTime >= :fromDate
      AND h.loginTime <= :toDate
    ORDER BY h.loginTime DESC
    """)
    Page<LoginHistory> filterLoginHistory(
            @Param("userId") Integer userId,
            @Param("browser") String browser,
            @Param("os") String os,
            @Param("ipAddress") String ipAddress,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
    SELECT h FROM LoginHistory h
    WHERE (:username = '' OR
           LOWER(h.user.username) LIKE LOWER(CONCAT('%', :username, '%')))
      AND (:browser = '' OR
           LOWER(h.browser) LIKE LOWER(CONCAT('%', :browser, '%')))
      AND (:os = '' OR
           LOWER(h.operatingSystem) LIKE LOWER(CONCAT('%', :os, '%')))
      AND (:ipAddress = '' OR
           h.ipAddress LIKE CONCAT('%', :ipAddress, '%'))
      AND (:status = '' OR
           CAST(h.loginStatus AS string) = :status)
      AND h.loginTime >= :fromDate
      AND h.loginTime <= :toDate
    ORDER BY h.loginTime DESC
    """)
    Page<LoginHistory> filterAllLoginHistory(
            @Param("username") String username,
            @Param("browser") String browser,
            @Param("os") String os,
            @Param("ipAddress") String ipAddress,
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
    SELECT h FROM LoginHistory h
    WHERE (:username = '' OR
           LOWER(h.user.username) LIKE LOWER(CONCAT('%', :username, '%')))
      AND (:browser = '' OR
           LOWER(h.browser) LIKE LOWER(CONCAT('%', :browser, '%')))
      AND (:os = '' OR
           LOWER(h.operatingSystem) LIKE LOWER(CONCAT('%', :os, '%')))
      AND (:ipAddress = '' OR
           h.ipAddress LIKE CONCAT('%', :ipAddress, '%'))
      AND (:status = '' OR
           CAST(h.loginStatus AS string) = :status)
      AND h.loginTime >= :fromDate
      AND h.loginTime <= :toDate
    ORDER BY h.loginTime DESC
    """)
    List<LoginHistory> findAllForReport( 
            @Param("username") String username,
            @Param("browser") String browser,
            @Param("os") String os,
            @Param("ipAddress") String ipAddress,
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}

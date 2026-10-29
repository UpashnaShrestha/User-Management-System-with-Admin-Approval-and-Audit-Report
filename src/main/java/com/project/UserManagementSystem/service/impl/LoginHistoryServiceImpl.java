package com.project.UserManagementSystem.service.impl;

import com.project.UserManagementSystem.dto.resposne.LoginHistoryResponseDto;
import com.project.UserManagementSystem.entity.LoginHistory;
import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.LoginStatus;
import com.project.UserManagementSystem.repo.LoginHistoryRepository;
import com.project.UserManagementSystem.repo.UserRepository;
import com.project.UserManagementSystem.service.LoginHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public void recordSuccessfulLogin(User user, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        LoginHistory history = new LoginHistory();
        history.setUser(user);
        history.setIpAddress(request.getRemoteAddr());
        history.setBrowser(getBrowser(userAgent));
        history.setOperatingSystem(getOperatingSystem(userAgent));
        history.setDevice(getDevice(userAgent));
        history.setLoginStatus(LoginStatus.SUCCESS);

        loginHistoryRepository.save(history);
        System.out.println(request.getHeader("User-Agent"));
    }

    @Override
    public String getBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("PostmanRuntime")) return "PostmanRuntime";
        if (userAgent.contains("Edg/")) return "Edge";
        if (userAgent.contains("Firefox/")) return "Firefox";
        if (userAgent.contains("Chrome/")) return "Chrome";
        if (userAgent.contains("Safari/")) return "Safari";

        return "Unknown";
    }

    private String getOperatingSystem(String userAgent) {
        if (userAgent == null) return "Unknown";

        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") ||
                userAgent.contains("iPad")) return "iOS";
        if (userAgent.contains("Mac OS X")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";

        return "Unknown";
    }

    private String getDevice(String userAgent) {
        if (userAgent == null) return "Unknown";

        if (userAgent.contains("iPad") ||
                userAgent.contains("Tablet")) return "Tablet";

        if (userAgent.contains("Mobi") ||
                userAgent.contains("Android") ||
                userAgent.contains("iPhone")) return "Mobile";

        return "Desktop";
    }


    @Override
    public Page<LoginHistoryResponseDto> getMyLoginHistory(String username, String browser, String os, String ipAddress, LocalDateTime fromDate, LocalDateTime toDate, int page, int size) {
        LocalDateTime effectiveFromDate =
                fromDate != null
                        ? fromDate
                        : LocalDateTime.of(2020, 1, 1, 0, 0);

        LocalDateTime effectiveToDate =
                toDate != null
                        ? toDate
                        : LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        User user = userRepository.findByUsername(username).orElseThrow(
                () ->
                        new UsernameNotFoundException("User not found.")
                );
        Pageable pageable = PageRequest.of(page, size);

        return loginHistoryRepository
                .filterLoginHistory(
                        user.getId(), normalize(browser), normalize(os), normalize(ipAddress), effectiveFromDate, effectiveToDate, pageable
                )
                .map(history -> new LoginHistoryResponseDto(
                        history.getId(),
                        history.getLoginTime(),
                        history.getIpAddress(),
                        history.getBrowser(),
                        history.getOperatingSystem(),
                        history.getDevice(),
                        history.getLoginStatus()
                ));
    }

    private String normalize(String value) {
        return value == null || value.isBlank()
                ? ""
                : value.trim();
    }

    @Override
    public Page<LoginHistoryResponseDto> getAllLoginHistory(String username, String browser, String os, String ipAddress, String status, LocalDateTime fromDate, LocalDateTime toDate, int page, int size) {
        LocalDateTime effectiveFromDate =
                fromDate != null
                        ? fromDate
                        : LocalDateTime.of(2020, 1, 1, 0, 0);

        LocalDateTime effectiveToDate =
                toDate != null
                        ? toDate
                        : LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        Pageable pageable = PageRequest.of(page, size);
        Page<LoginHistory> histories = loginHistoryRepository.filterAllLoginHistory(
                normalize(username),
                normalize(browser),
                normalize(os),
                normalize(ipAddress),
                normalize(status),
                effectiveFromDate, effectiveToDate, pageable
        );
        return histories.map(history -> new LoginHistoryResponseDto(
                        history.getId(),
                        history.getLoginTime(),
                        history.getIpAddress(),
                        history.getBrowser(),
                        history.getOperatingSystem(),
                        history.getDevice(),
                        history.getLoginStatus()
        ));
    }

    @Override
    public Page<LoginHistoryResponseDto> getUserLoginHistory(Integer userId, String browser, String os, String ipAddress, LocalDateTime fromDate, LocalDateTime toDate, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(
                ()  -> new UsernameNotFoundException("User not found.")
        );
        return getMyLoginHistory(user.getUsername(), browser, os, ipAddress, fromDate, toDate, page, size);
    }
}

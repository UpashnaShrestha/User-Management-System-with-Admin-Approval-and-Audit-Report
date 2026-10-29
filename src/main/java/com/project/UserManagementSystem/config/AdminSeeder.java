package com.project.UserManagementSystem.config;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.RoleType;
import com.project.UserManagementSystem.enums.UserStatus;
import com.project.UserManagementSystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }

        User user = new User();

        user.setFirstName("System");
        user.setLastName("Admin");
        user.setUsername("admin");
        user.setEmail("upashnashrestha1.com");

        user.setPassword(
                passwordEncoder.encode("Admin@123")
        );

        user.setRole(RoleType.ADMIN);
        user.setStatus(UserStatus.APPROVED);
        user.setApprovedAt(LocalDateTime.now());

        userRepository.save(user);

        System.out.println("Initial admin account created.");
    }
}
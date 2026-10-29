package com.project.UserManagementSystem.service;


import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

//    public CustomUserDetails(User user) {
//        this.user = user;
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Appends the role string cleanly (e.g., ROLE_STUDENT)
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword() != null ? user.getPassword() : "";
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account never expires
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account is not locked out
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Passwords never expire internally
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.APPROVED;
    }
}


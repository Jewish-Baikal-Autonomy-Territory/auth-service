package org.example.ai_auth_service.services;

import org.example.ai_auth_service.entity.Role;
import org.example.ai_auth_service.entity.User;
import org.example.ai_auth_service.repository.JwtRepository;
import org.example.ai_auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private JwtRepository jwtRepository;

    public User save(User user) {
        return repository.save(user);
    }

    public User create(User user) {
        if (repository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("User with this phone number already exists");
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        return save(user);
    }

    public User getByUsername(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ADMIN);
        save(user);
    }
}
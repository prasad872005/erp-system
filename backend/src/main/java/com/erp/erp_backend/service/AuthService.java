package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.AuthRequest;
import com.erp.erp_backend.dto.AuthResponse;
import com.erp.erp_backend.entity.Role;
import com.erp.erp_backend.entity.User;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.repository.UserRepository;
import com.erp.erp_backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(
            String username,
            String email,
            String password) {

        if (userRepository.existsByUsername(username)) {
            throw new ResourceConflictException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResourceConflictException(
                    "Email already exists"
            );
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.SALES_EXECUTIVE)
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }

    public AuthResponse login(AuthRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid username or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Invalid username or password"
            );
        }

        if (!user.isEnabled()) {
            throw new RuntimeException(
                    "User account is disabled"
            );
        }

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }
}
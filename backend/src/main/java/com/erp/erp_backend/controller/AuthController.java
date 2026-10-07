package com.erp.erp_backend.controller;

import com.erp.erp_backend.dto.AuthRequest;
import com.erp.erp_backend.dto.AuthResponse;
import com.erp.erp_backend.dto.RegisterRequest;
import com.erp.erp_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(
                        request.getUsername(),
                        request.getEmail(),
                        request.getPassword()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
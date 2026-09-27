package com.example.kanban_api.controller;

import com.example.kanban_api.dto.AuthTokenResponse;
import com.example.kanban_api.dto.LoginRequest;
import com.example.kanban_api.dto.RegisterRequest;
import com.example.kanban_api.dto.UserResponse;
import com.example.kanban_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Routes publiques, 201 à l'inscription et 200 au login @Valid s'applique uniquement à l'inscription
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthTokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}

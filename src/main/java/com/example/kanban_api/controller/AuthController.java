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

/**
 * Controller d'authentification (= routes HTTP).
 *
 * Équivalents :
 * - Symfony : AuthController + #[Route]
 * - NestJS : AuthController + @Controller('auth')
 *
 * @RestController = renvoie du JSON automatiquement
 * @RequestMapping = préfixe commun des routes (/api/auth)
 *
 * Ces routes sont PUBLIQUES (voir SecurityConfiguration).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Injection par constructeur (= autowire Symfony / constructor DI Nest)
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/register → crée un compte (201 Created) */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        // @Valid = vérifie les contraintes du DTO (@Email, @NotBlank...)
        // @RequestBody = lit le JSON du body (comme Request en Symfony)
        return authService.register(request);
    }

    /** POST /api/auth/login → renvoie un JWT (200 OK) */
    @PostMapping("/login")
    public AuthTokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}

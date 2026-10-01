package com.example.kanban_api.controller;

import com.example.kanban_api.dto.UpdateUserRequest;
import com.example.kanban_api.dto.UserResponse;
import com.example.kanban_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller utilisateurs.
 *
 * Équivalents : UserController Symfony / NestJS.
 * Le token JWT est exigé par Spring Security (pas dans ce fichier).
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** GET /api/users/me → profil de l'utilisateur connecté */
    @GetMapping("/me")
    public UserResponse me() {
        return userService.me();
    }

    /**
     * PATCH /api/users/{id} → modifier un user.
     * {id} est récupéré via @PathVariable (= $id dans Symfony, @Param Nest).
     * Droits vérifiés dans le service (soi-même ou admin).
     */
    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }
}

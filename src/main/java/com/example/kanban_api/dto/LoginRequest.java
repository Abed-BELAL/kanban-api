package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Corps de POST /api/auth/login.
 * Pas de @Valid volontairement : un champ vide → 401 générique
 * (on ne révèle pas si c'est l'email ou le mdp qui manque).
 */
@Getter
@Setter
public class LoginRequest {

    private String email;
    private String password;
}

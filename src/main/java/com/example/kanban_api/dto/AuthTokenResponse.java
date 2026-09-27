package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

// Réponse de POST /api/auth/login /un seul champ : le JWT
@Getter
@Setter
public class AuthTokenResponse {

    private String accessToken;
}

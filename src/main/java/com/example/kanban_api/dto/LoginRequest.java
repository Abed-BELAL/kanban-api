package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

// Corps de POST /api/auth/login Pas de @Valid : un champ vide reste un 401 générique
@Getter
@Setter
public class LoginRequest {

    private String email;
    private String password;
}

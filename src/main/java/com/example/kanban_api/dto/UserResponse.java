package com.example.kanban_api.dto;

import com.example.kanban_api.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

// Réponse publique, aucun champ password : le hash ne peut pas sortir par cette classe
@Getter
@Setter
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private Role role;
    private Instant createdAt;
}

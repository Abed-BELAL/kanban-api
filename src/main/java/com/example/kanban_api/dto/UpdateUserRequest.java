package com.example.kanban_api.dto;

import com.example.kanban_api.model.Role;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

// Champs optionnels de PATCH /api/users/{id}, role n'est modifiable que par un admin
@Getter
@Setter
public class UpdateUserRequest {

    private String name;

    @Email(message = "email invalide")
    private String email;

    private Role role;
}

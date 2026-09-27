package com.example.kanban_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Corps de POST /api/auth/register, le seuil de 8 caractères n'est pas fixé par le contrat
@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "email obligatoire")
    @Email(message = "email invalide")
    private String email;

    @NotBlank(message = "mot de passe obligatoire")
    @Size(min = 8, message = "mot de passe trop court")
    private String password;

    @NotBlank(message = "nom obligatoire")
    private String name;
}

package com.example.kanban_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO = objet de transfert (données reçues / envoyées en JSON).
 *
 * Équivalents :
 * - Symfony : DTO / Input class + Assert constraints
 * - NestJS : class + class-validator (@IsEmail, @IsNotBlank...)
 *
 * On ne expose PAS l'entité User directement : le DTO contrôle ce qui entre/sort.
 * Ici : corps de POST /api/auth/register.
 */
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

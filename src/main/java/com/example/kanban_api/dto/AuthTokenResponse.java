package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

/** Réponse du login : uniquement le JWT (accessToken). */
@Getter
@Setter
public class AuthTokenResponse {

    private String accessToken;
}

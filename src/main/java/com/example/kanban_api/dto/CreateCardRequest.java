package com.example.kanban_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Corps de POST /api/lists/{listId}/cards description et position sont optionnelles
@Getter
@Setter
public class CreateCardRequest {

    @NotBlank(message = "titre manquant")
    private String title;

    private String description;

    private Integer position;
}

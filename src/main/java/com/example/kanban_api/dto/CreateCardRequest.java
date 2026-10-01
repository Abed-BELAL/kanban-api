package com.example.kanban_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Corps de POST /api/lists/{listId}/cards. Seul le titre est obligatoire. */
@Getter
@Setter
public class CreateCardRequest {

    @NotBlank(message = "titre manquant")
    private String title;

    private String description;

    private Integer position;
}

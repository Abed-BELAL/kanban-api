package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Corps de PATCH /api/cards/{id} (tous les champs optionnels).
 * listId = déplacer la carte vers une autre colonne.
 */
@Getter
@Setter
public class UpdateCardRequest {

    private String title;
    private String description;
    private Integer position;
    private Long listId;
}

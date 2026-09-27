package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

// Champs optionnels de PATCH /api/cards/{id}, listId déplace la carte vers une autre liste
@Getter
@Setter
public class UpdateCardRequest {

    private String title;
    private String description;
    private Integer position;
    private Long listId;
}

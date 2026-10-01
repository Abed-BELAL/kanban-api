package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

/** Corps de PATCH /api/lists/{id}. Champs optionnels (mise à jour partielle). */
@Getter
@Setter
public class UpdateKanbanListRequest {

    private String title;
    private Integer position;
}

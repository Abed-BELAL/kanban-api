package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

// Champs optionnels de PATCH /api/lists/{id}, un titre vide est refusé dans le service
@Getter
@Setter
public class UpdateKanbanListRequest {

    private String title;
    private Integer position;
}

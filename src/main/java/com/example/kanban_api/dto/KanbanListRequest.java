package com.example.kanban_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Corps de POST /api/lists. L'id est généré en BDD, pas envoyé par le client. */
@Getter
@Setter
public class KanbanListRequest {

    @NotBlank
    private String title;
    private Integer position;
}

package com.example.kanban_api.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// JSON accepté à la création Pas d'id qui lui est généré en bdd
// @NotBlank utile pour refuser un titre vide avant d'entrer dans le service
@Getter
@Setter
public class KanbanListRequest {

    @NotBlank
    private String title;
    private Integer position;
}
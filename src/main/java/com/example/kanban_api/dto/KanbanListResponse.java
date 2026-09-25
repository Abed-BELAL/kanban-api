package com.example.kanban_api.dto;
import lombok.Getter;
import lombok.Setter;

    // JSON renvoyé au client l'entité n'est pas sérialisée pour choisir les champs qui sortent de l'API.
@Getter
@Setter
public class KanbanListResponse {
    private Long id;
    private String title;
    private Integer position;
}
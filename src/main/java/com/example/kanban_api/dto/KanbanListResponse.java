package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** JSON renvoyé pour une liste. On n'expose jamais l'entité JPA brute. */
@Getter
@Setter
public class KanbanListResponse {

    private Long id;
    private String title;
    private Integer position;
    private Long ownerId;
    private Instant createdAt;
}

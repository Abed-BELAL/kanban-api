package com.example.kanban_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** JSON renvoyé pour une carte. On n'expose jamais l'entité JPA brute. */
@Getter
@Setter
public class CardResponse {

    private Long id;
    private String title;
    private String description;
    private Integer position;
    private Long listId;
    private Instant createdAt;
    private Instant updatedAt;
}

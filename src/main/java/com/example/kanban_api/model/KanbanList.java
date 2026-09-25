package com.example.kanban_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

// Table kanban_list crée grace à Hibernate au démarrage via  ddl-auto: update.
// IDENTITY : PostgreSQL génère automatiquement l'id le client ne le choisit pas.
@Entity
@Getter
@Setter
public class KanbanList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer position;
}
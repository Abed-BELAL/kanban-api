package com.example.kanban_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Entité Liste Kanban (= une colonne du board : "À faire", "En cours"...).
 * ownerId = id de l'auteur : seul lui peut lire / modifier / supprimer.
 */
@Entity
@Getter
@Setter
public class KanbanList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer position; // ordre d'affichage

    private Long ownerId; // auteur de la liste

    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}

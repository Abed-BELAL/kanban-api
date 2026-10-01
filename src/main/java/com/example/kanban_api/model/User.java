package com.example.kanban_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Entité User = table "users" en BDD.
 *
 * Équivalents :
 * - Symfony : Entity Doctrine (@ORM\Entity)
 * - NestJS : Entity TypeORM (@Entity())
 *
 * @Entity = classe liée à une table
 * @Getter/@Setter = Lombok génère les get/set (évite le boilerplate)
 * Le mot de passe stocké est un hash BCrypt, jamais renvoyé par l'API.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // hash, pas le mot de passe en clair

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // stocke "user" / "admin" en texte
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Instant createdAt;

    /** Avant le premier INSERT, on pose la date de création automatiquement. */
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}

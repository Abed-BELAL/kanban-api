package com.example.kanban_api.model;

/**
 * Rôles possibles d'un utilisateur.
 * enum = liste fixe de valeurs (comme une enum PHP/TS).
 * Stocké en texte en BDD grâce à @Enumerated(EnumType.STRING) sur User.
 */
public enum Role {
    user,
    admin
}

package com.example.kanban_api.exception;

/** Exception métier → transformée en 404 par ApiExceptionHandler. */
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Ressource introuvable");
    }
}

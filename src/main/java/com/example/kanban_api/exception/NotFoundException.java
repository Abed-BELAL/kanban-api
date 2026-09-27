package com.example.kanban_api.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Ressource introuvable");
    }
}

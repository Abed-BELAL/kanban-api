package com.example.kanban_api.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Accès refusé");
    }
}

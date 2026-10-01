package com.example.kanban_api.exception;

/** Exception métier → transformée en 403 par ApiExceptionHandler. */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Accès refusé");
    }
}

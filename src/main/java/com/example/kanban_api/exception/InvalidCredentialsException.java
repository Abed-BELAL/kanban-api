package com.example.kanban_api.exception;

/** Exception métier → transformée en 401 par ApiExceptionHandler. */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Identifiants invalides");
    }
}

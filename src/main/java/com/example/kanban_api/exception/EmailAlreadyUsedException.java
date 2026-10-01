package com.example.kanban_api.exception;

/** Exception métier → transformée en 409 Conflict par ApiExceptionHandler. */
public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException() {
        super("Email déjà utilisé");
    }
}

package com.example.kanban_api.exception;

/**
 * Exception métier pour un champ invalide (ex: titre vide).
 * → transformée en 400 avec { field, message } par ApiExceptionHandler.
 */
public class InvalidPayloadException extends RuntimeException {

    private final String field;

    public InvalidPayloadException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}

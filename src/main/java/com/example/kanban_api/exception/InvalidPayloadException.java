package com.example.kanban_api.exception;

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

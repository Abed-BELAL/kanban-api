package com.example.kanban_api.exception;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException() {
        super("Email déjà utilisé");
    }
}

package com.example.kanban_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire d'erreurs global.
 *
 * Équivalents :
 * - Symfony : EventSubscriber / ExceptionListener
 * - NestJS : ExceptionFilter
 *
 * @RestControllerAdvice = intercepte les exceptions de tous les controllers
 * et les transforme en réponses JSON propres (400, 401, 403, 404, 409...).
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** Erreurs de validation (@Valid sur un DTO) → 400 + liste des champs. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException exception) {
        List<Map<String, String>> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    Map<String, String> field = new LinkedHashMap<>();
                    field.put("field", error.getField());
                    field.put("message", error.getDefaultMessage());
                    return field;
                })
                .toList();
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    /** Email déjà utilisé à l'inscription → 409 Conflict */
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, String>> conflict(EmailAlreadyUsedException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", exception.getMessage()));
    }

    /** Mauvais identifiants au login → 401 */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> unauthorized(InvalidCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", exception.getMessage()));
    }

    /** Pas le droit (ex: liste d'un autre user) → 403 */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> forbidden(ForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", exception.getMessage()));
    }

    /** Ressource introuvable → 404 */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    /** Données invalides côté métier (ex: titre vide en PATCH) → 400 */
    @ExceptionHandler(InvalidPayloadException.class)
    public ResponseEntity<Map<String, Object>> invalidPayload(InvalidPayloadException exception) {
        Map<String, String> field = new LinkedHashMap<>();
        field.put("field", exception.getField());
        field.put("message", exception.getMessage());
        return ResponseEntity.badRequest().body(Map.of("errors", java.util.List.of(field)));
    }

    /** JSON illisible : sur /login → 401, sinon → 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> unreadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        if ("/api/auth/login".equals(request.getRequestURI())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Identifiants invalides"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "payload invalide"));
    }
}

package com.example.kanban_api.controller;

import com.example.kanban_api.dto.KanbanListRequest;
import com.example.kanban_api.dto.KanbanListResponse;
import com.example.kanban_api.service.KanbanListService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

// Point d'entrée HTTP. 201 à la création, 204 à la suppression (succès, corps vide).
// @Valid déclenche le @NotBlank du DTO de requête.
@RestController
@RequestMapping("/api/lists")
public class KanbanListController {

    private final KanbanListService service;

    public KanbanListController(KanbanListService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KanbanListResponse create(@Valid @RequestBody KanbanListRequest request) {
        return service.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
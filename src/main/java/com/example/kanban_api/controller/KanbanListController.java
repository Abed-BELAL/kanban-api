package com.example.kanban_api.controller;

import com.example.kanban_api.dto.KanbanListRequest;
import com.example.kanban_api.dto.KanbanListResponse;
import com.example.kanban_api.dto.UpdateKanbanListRequest;
import com.example.kanban_api.service.KanbanListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Point d'entrée HTTP des listes, 201 à la création, 204 à la suppression
@RestController
@RequestMapping("/api/lists")
public class KanbanListController {

    private final KanbanListService service;

    public KanbanListController(KanbanListService service) {
        this.service = service;
    }

    @GetMapping
    public List<KanbanListResponse> findMine() {
        return service.findMine();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KanbanListResponse create(@Valid @RequestBody KanbanListRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{id}")
    public KanbanListResponse update(@PathVariable Long id, @RequestBody UpdateKanbanListRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

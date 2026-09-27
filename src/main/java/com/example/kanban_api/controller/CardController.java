package com.example.kanban_api.controller;

import com.example.kanban_api.dto.CardResponse;
import com.example.kanban_api.dto.CreateCardRequest;
import com.example.kanban_api.dto.UpdateCardRequest;
import com.example.kanban_api.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Routes cartes, celles préfixées par /api/lists/{listId} vérifient d'abord que la liste m'appartient
@RestController
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/api/lists/{listId}/cards")
    public List<CardResponse> findByList(@PathVariable Long listId) {
        return cardService.findByList(listId);
    }

    @PostMapping("/api/lists/{listId}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse create(@PathVariable Long listId, @Valid @RequestBody CreateCardRequest request) {
        return cardService.create(listId, request);
    }

    @GetMapping("/api/cards/{id}")
    public CardResponse findOne(@PathVariable Long id) {
        return cardService.findOne(id);
    }

    @PatchMapping("/api/cards/{id}")
    public CardResponse update(@PathVariable Long id, @RequestBody UpdateCardRequest request) {
        return cardService.update(id, request);
    }

    @DeleteMapping("/api/cards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cardService.delete(id);
    }
}

package com.example.kanban_api.service;

import com.example.kanban_api.dto.CardResponse;
import com.example.kanban_api.dto.CreateCardRequest;
import com.example.kanban_api.dto.UpdateCardRequest;
import com.example.kanban_api.exception.InvalidPayloadException;
import com.example.kanban_api.exception.NotFoundException;
import com.example.kanban_api.model.Card;
import com.example.kanban_api.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Les droits viennent de la liste parente, déplacer une carte exige d'être auteur de la liste cible
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final KanbanListService kanbanListService;

    public CardService(CardRepository cardRepository, KanbanListService kanbanListService) {
        this.cardRepository = cardRepository;
        this.kanbanListService = kanbanListService;
    }

    public List<CardResponse> findByList(Long listId) {
        kanbanListService.ownedOrThrow(listId);
        return cardRepository.findByListId(listId).stream().map(this::toResponse).toList();
    }

    public CardResponse create(Long listId, CreateCardRequest request) {
        kanbanListService.ownedOrThrow(listId);
        Card card = new Card();
        card.setTitle(request.getTitle());
        card.setDescription(request.getDescription());
        card.setPosition(request.getPosition());
        card.setListId(listId);
        return toResponse(cardRepository.save(card));
    }

    public CardResponse findOne(Long id) {
        return toResponse(ownedCard(id));
    }

    @Transactional
    public CardResponse update(Long id, UpdateCardRequest request) {
        Card card = ownedCard(id);
        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) {
                throw new InvalidPayloadException("title", "titre vide");
            }
            card.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            card.setDescription(request.getDescription());
        }
        if (request.getPosition() != null) {
            card.setPosition(request.getPosition());
        }
        if (request.getListId() != null) {
            kanbanListService.ownedOrThrow(request.getListId());
            card.setListId(request.getListId());
        }
        return toResponse(cardRepository.save(card));
    }

    public void delete(Long id) {
        ownedCard(id);
        cardRepository.deleteById(id);
    }

    private Card ownedCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(NotFoundException::new);
        kanbanListService.ownedOrThrow(card.getListId());
        return card;
    }

    private CardResponse toResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setTitle(card.getTitle());
        response.setDescription(card.getDescription());
        response.setPosition(card.getPosition());
        response.setListId(card.getListId());
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());
        return response;
    }
}

package com.example.kanban_api.service;

import com.example.kanban_api.dto.KanbanListRequest;
import com.example.kanban_api.dto.KanbanListResponse;
import com.example.kanban_api.dto.UpdateKanbanListRequest;
import com.example.kanban_api.exception.ForbiddenException;
import com.example.kanban_api.exception.InvalidPayloadException;
import com.example.kanban_api.exception.NotFoundException;
import com.example.kanban_api.model.KanbanList;
import com.example.kanban_api.repository.CardRepository;
import com.example.kanban_api.repository.KanbanListRepository;
import com.example.kanban_api.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service des listes Kanban (colonnes).
 *
 * Règle forte : une liste n'appartient qu'à son auteur (ownerId).
 * - id inconnu → 404
 * - liste d'un autre user → 403
 *
 * ownedOrThrow() est aussi utilisé par CardService pour vérifier les droits.
 */
@Service
public class KanbanListService {

    private final KanbanListRepository repository;
    private final CardRepository cardRepository;

    public KanbanListService(KanbanListRepository repository, CardRepository cardRepository) {
        this.repository = repository;
        this.cardRepository = cardRepository;
    }

    /** Liste uniquement les colonnes du user connecté. */
    public List<KanbanListResponse> findMine() {
        return repository.findByOwnerId(CurrentUser.id()).stream().map(this::toResponse).toList();
    }

    public KanbanListResponse create(KanbanListRequest request) {
        KanbanList list = new KanbanList();
        list.setTitle(request.getTitle());
        list.setPosition(request.getPosition());
        list.setOwnerId(CurrentUser.id()); // on force l'auteur = user connecté
        return toResponse(repository.save(list));
    }

    @Transactional
    public KanbanListResponse update(Long id, UpdateKanbanListRequest request) {
        KanbanList list = ownedOrThrow(id);
        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) {
                throw new InvalidPayloadException("title", "titre vide");
            }
            list.setTitle(request.getTitle());
        }
        if (request.getPosition() != null) {
            list.setPosition(request.getPosition());
        }
        return toResponse(repository.save(list));
    }

    /**
     * Supprime une liste ET ses cartes (cascade manuelle).
     * Transaction = si une étape échoue, rien n'est gardé.
     */
    @Transactional
    public void delete(Long id) {
        ownedOrThrow(id);
        cardRepository.deleteByListId(id); // d'abord les cartes
        repository.deleteById(id);         // puis la liste
    }

    /**
     * Vérifie que la liste existe ET appartient au user connecté.
     * Méthode publique car réutilisée par CardService.
     */
    public KanbanList ownedOrThrow(Long id) {
        KanbanList list = repository.findById(id).orElseThrow(NotFoundException::new);
        if (!list.getOwnerId().equals(CurrentUser.id())) {
            throw new ForbiddenException();
        }
        return list;
    }

    private KanbanListResponse toResponse(KanbanList list) {
        KanbanListResponse response = new KanbanListResponse();
        response.setId(list.getId());
        response.setTitle(list.getTitle());
        response.setPosition(list.getPosition());
        response.setOwnerId(list.getOwnerId());
        response.setCreatedAt(list.getCreatedAt());
        return response;
    }
}

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

// Une liste n'est visible et modifiable que par son auteur, id inconnu : 404, autre auteur : 403
@Service
public class KanbanListService {

    private final KanbanListRepository repository;
    private final CardRepository cardRepository;

    public KanbanListService(KanbanListRepository repository, CardRepository cardRepository) {
        this.repository = repository;
        this.cardRepository = cardRepository;
    }

    public List<KanbanListResponse> findMine() {
        return repository.findByOwnerId(CurrentUser.id()).stream().map(this::toResponse).toList();
    }

    public KanbanListResponse create(KanbanListRequest request) {
        KanbanList list = new KanbanList();
        list.setTitle(request.getTitle());
        list.setPosition(request.getPosition());
        list.setOwnerId(CurrentUser.id());
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

    @Transactional
    public void delete(Long id) {
        ownedOrThrow(id);
        cardRepository.deleteByListId(id);
        repository.deleteById(id);
    }

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

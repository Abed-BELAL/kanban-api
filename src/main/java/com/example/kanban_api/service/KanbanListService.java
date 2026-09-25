package com.example.kanban_api.service;

import com.example.kanban_api.dto.KanbanListRequest;
import com.example.kanban_api.dto.KanbanListResponse;
import com.example.kanban_api.model.KanbanList;
import com.example.kanban_api.repository.KanbanListRepository;
import org.springframework.stereotype.Service;

// Règle métier le controller se contente d'appeler ces méthodes
// Le repository est fourni par Spring via le constructeur
@Service
public class KanbanListService {

    private final KanbanListRepository repository;

    public KanbanListService(KanbanListRepository repository) {
        this.repository = repository;
    }

    public KanbanListResponse create(KanbanListRequest request) {
        KanbanList list = new KanbanList();
        list.setTitle(request.getTitle());
        list.setPosition(request.getPosition());
        return toResponse(repository.save(list));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Traduction interne entité -> réponse elle est Privatecar ce n'est pas une route.
    private KanbanListResponse toResponse(KanbanList list) {
        KanbanListResponse response = new KanbanListResponse();
        response.setId(list.getId());
        response.setTitle(list.getTitle());
        response.setPosition(list.getPosition());
        return response;
    }
}
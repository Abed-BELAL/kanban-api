package com.example.kanban_api.repository;

import com.example.kanban_api.model.KanbanList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Accès BDD aux listes Kanban.
 * findByOwnerId = "trouve les listes dont l'auteur est X"
 * (Spring Data écrit la requête automatiquement).
 */
public interface KanbanListRepository extends JpaRepository<KanbanList, Long> {

    List<KanbanList> findByOwnerId(Long ownerId);
}

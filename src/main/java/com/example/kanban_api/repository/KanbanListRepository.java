package com.example.kanban_api.repository;

import com.example.kanban_api.model.KanbanList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Spring Data fournit save, findById et deleteById, findByOwnerId limite la lecture à l'auteur
public interface KanbanListRepository extends JpaRepository<KanbanList, Long> {

    List<KanbanList> findByOwnerId(Long ownerId);
}

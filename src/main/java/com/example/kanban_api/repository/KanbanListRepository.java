package com.example.kanban_api.repository;

import com.example.kanban_api.model.KanbanList;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface vide : Spring Data fournit save, findById et deleteById.
// KanbanList est le type de la ligne, Long est le type de l'id.
public interface KanbanListRepository extends JpaRepository<KanbanList, Long> {
}

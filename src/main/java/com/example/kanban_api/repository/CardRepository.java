package com.example.kanban_api.repository;

import com.example.kanban_api.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// deleteByListId retire les cartes quand leur liste est supprimée
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByListId(Long listId);

    void deleteByListId(Long listId);
}

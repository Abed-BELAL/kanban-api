package com.example.kanban_api.repository;

import com.example.kanban_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Accès BDD aux users.
 *
 * Équivalents :
 * - Symfony : UserRepository (Doctrine)
 * - NestJS : UserRepository / TypeORM Repository
 *
 * JpaRepository donne déjà save, findById, deleteById, etc.
 * On ajoute juste les méthodes custom : Spring génère le SQL
 * à partir du nom (findByEmail → WHERE email = ?).
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}

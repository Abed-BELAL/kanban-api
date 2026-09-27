package com.example.kanban_api.repository;

import com.example.kanban_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data fournit save et findById, l'email sert à l'inscription et au login
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}

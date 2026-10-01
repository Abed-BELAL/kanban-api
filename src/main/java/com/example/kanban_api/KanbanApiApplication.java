package com.example.kanban_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application (comme public/index.php côté Symfony,
 * ou main.ts côté NestJS).
 *
 * @SpringBootApplication = "démarre Spring + scanne les classes du projet".
 * Spring crée tout seul les objets (controllers, services...) grâce à
 * l'injection de dépendances (= le conteneur DI de Symfony / NestJS).
 */
@SpringBootApplication
public class KanbanApiApplication {

	static void main(String[] args) {
		// Lance le serveur HTTP (ici sur le port 3000, voir application.yml)
		SpringApplication.run(KanbanApiApplication.class, args);
	}

}

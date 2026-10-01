package com.example.kanban_api.service;

import com.example.kanban_api.dto.AuthTokenResponse;
import com.example.kanban_api.dto.LoginRequest;
import com.example.kanban_api.dto.RegisterRequest;
import com.example.kanban_api.dto.UserResponse;
import com.example.kanban_api.exception.EmailAlreadyUsedException;
import com.example.kanban_api.exception.InvalidCredentialsException;
import com.example.kanban_api.model.Role;
import com.example.kanban_api.model.User;
import com.example.kanban_api.repository.UserRepository;
import com.example.kanban_api.security.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logique métier de l'auth (inscription + connexion).
 *
 * Équivalents :
 * - Symfony : AuthService / UserManager
 * - NestJS : AuthService
 *
 * @Service = classe métier gérée par Spring (injection possible partout).
 * Le controller appelle ici, ici on parle à la BDD et au JWT.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    // Hash factice : sert à éviter une fuite de timing si l'email n'existe pas
    // (on compare toujours un mot de passe, même si le user est inconnu)
    private final String dummyHash;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyHash = passwordEncoder.encode("timing-equalizer");
    }

    /**
     * Inscription.
     * @Transactional = si une erreur arrive en cours de route, on annule tout
     * (comme une transaction Doctrine / TypeORM).
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Email déjà pris → 409 Conflict
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException();
        }

        User user = new User();
        user.setEmail(request.getEmail());
        // On stocke le HASH, jamais le mot de passe en clair
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(Role.user); // rôle par défaut

        try {
            // saveAndFlush = écrit tout de suite en BDD (pour catcher le conflit email)
            return toResponse(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            // Filet de sécurité si 2 inscriptions simultanées avec le même email
            throw new EmailAlreadyUsedException();
        }
    }

    /** Connexion : vérifie email+mdp, puis renvoie un JWT. */
    public AuthTokenResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null
                || request.getEmail().isBlank() || request.getPassword().isBlank()) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            // Compare quand même pour garder un temps de réponse similaire
            passwordEncoder.matches(request.getPassword(), dummyHash);
            throw new InvalidCredentialsException(); // 401
        }
        // matches = compare le mdp saisi avec le hash en BDD
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        AuthTokenResponse response = new AuthTokenResponse();
        response.setAccessToken(jwtService.generate(user));
        return response;
    }

    /** Convertit l'entité User → DTO public (sans le mot de passe). */
    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}

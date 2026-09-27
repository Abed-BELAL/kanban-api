package com.example.kanban_api.service;

import com.example.kanban_api.dto.UpdateUserRequest;
import com.example.kanban_api.dto.UserResponse;
import com.example.kanban_api.exception.ForbiddenException;
import com.example.kanban_api.exception.InvalidPayloadException;
import com.example.kanban_api.exception.NotFoundException;
import com.example.kanban_api.model.User;
import com.example.kanban_api.repository.UserRepository;
import com.example.kanban_api.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Profil et droits, 404 si l'id n'existe pas, 403 si ce n'est ni soi ni un admin, 403 si un non-admin touche au role
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse me() {
        return toResponse(findOrThrow(CurrentUser.id()));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findOrThrow(id);
        boolean self = user.getId().equals(CurrentUser.id());
        if (!self && !CurrentUser.isAdmin()) {
            throw new ForbiddenException();
        }
        if (request.getRole() != null && !CurrentUser.isAdmin()) {
            throw new ForbiddenException();
        }
        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new InvalidPayloadException("name", "name est obligatoire");
            }
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            if (request.getEmail().isBlank()) {
                throw new InvalidPayloadException("email", "email invalide");
            }
            if (userRepository.existsByEmail(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
                throw new InvalidPayloadException("email", "email invalide");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        return toResponse(userRepository.save(user));
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

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

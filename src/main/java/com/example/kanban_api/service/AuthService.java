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

// Inscription et connexion, le hash BCrypt reste en base, la réponse ne porte que le DTO public
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String dummyHash;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyHash = passwordEncoder.encode("timing-equalizer");
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException();
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(Role.user);

        try {
            return toResponse(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyUsedException();
        }
    }

    public AuthTokenResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null
                || request.getEmail().isBlank() || request.getPassword().isBlank()) {
            throw new InvalidCredentialsException();
        }

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            passwordEncoder.matches(request.getPassword(), dummyHash);
            throw new InvalidCredentialsException();
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        AuthTokenResponse response = new AuthTokenResponse();
        response.setAccessToken(jwtService.generate(user));
        return response;
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

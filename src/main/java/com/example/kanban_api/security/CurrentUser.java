package com.example.kanban_api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Petit helper pour savoir "qui est connecté" dans les services.
 *
 * Équivalents :
 * - Symfony : $this->getUser() / Security::getUser()
 * - NestJS : @Req() req.user / @CurrentUser() décorateur custom
 *
 * L'id vient du claim "sub" du JWT, posé par JwtAuthenticationFilter.
 */
public final class CurrentUser {

    // Classe utilitaire : pas d'instance
    private CurrentUser() {
    }

    /** Renvoie l'id de l'utilisateur connecté. */
    public static Long id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getPrincipal().toString());
    }

    /** true si le user a le rôle admin. */
    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}

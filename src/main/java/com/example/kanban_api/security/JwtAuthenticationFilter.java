package com.example.kanban_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre exécuté à CHAQUE requête HTTP.
 *
 * Équivalents :
 * - Symfony : JWTAuthenticator / firewall
 * - NestJS : AuthGuard('jwt') + middleware
 *
 * Rôle : lire le header "Authorization: Bearer xxx", vérifier le token,
 * et "connecter" l'utilisateur pour la suite de la requête.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 1) On lit le header Authorization
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        // Pas de Bearer → on laisse passer (la sécurité décidera plus tard si 401)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2) On décode le token (substring(7) = on enlève "Bearer ")
            Jwt jwt = jwtService.decode(header.substring(7));
            // 3) On récupère le rôle et on le convertit au format Spring (ROLE_xxx)
            String role = jwt.getClaimAsString("role");
            String authority = "admin".equals(role) ? "ROLE_ADMIN" : "ROLE_USER";
            // 4) On crée l'objet "utilisateur connecté" pour Spring Security
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    jwt.getSubject(), // principal = id user (claim "sub")
                    null,
                    List.of(new SimpleGrantedAuthority(authority))
            );
            // 5) On le range dans le contexte (= $this->getUser() Symfony / req.user Nest)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException exception) {
            // Token invalide / expiré → 401
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"Authentification manquante ou invalide\"}");
        }
    }
}

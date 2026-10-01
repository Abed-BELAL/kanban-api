package com.example.kanban_api.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Config de sécurité globale.
 *
 * Équivalents :
 * - Symfony : security.yaml + firewalls + access_control
 * - NestJS : AuthGuard + JwtModule + middleware CORS
 *
 * Ici : API "stateless" = pas de session serveur, on se fie au JWT à chaque requête.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Encodeur de mots de passe BCrypt.
     * Même idée que password_hash() / UserPasswordHasherInterface (Symfony)
     * ou bcrypt dans NestJS (passport / bcrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Chaîne de filtres de sécurité : qui peut accéder à quoi.
     * C'est le cœur de Spring Security.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
                // CORS : autorise le front Angular (localhost:4200) à appeler l'API
                .cors(Customizer.withDefaults())
                // CSRF désactivé car on est en API JWT (pas de cookies de session)
                .csrf(AbstractHttpConfigurer::disable)
                // Pas de login HTML classique (Basic / formulaire)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // STATELESS = pas de session HTTP côté serveur (comme une API Nest JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Règles d'accès aux routes
                .authorizeHttpRequests(auth -> auth
                        // Inscription + connexion : publiques (pas besoin de token)
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        // Page Swagger : publique
                        .requestMatchers(HttpMethod.GET, "/api").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        // Tout le reste : il faut être connecté (JWT valide)
                        .anyRequest().authenticated()
                )
                // Si pas de token / token invalide → réponse 401 JSON
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"message\":\"Authentification manquante ou invalide\"}");
                }))
                // On place notre filtre JWT AVANT le filtre login classique de Spring
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Config CORS (= nelmio/cors-bundle en Symfony, enableCors() en NestJS).
     * Sans ça, le navigateur bloque les appels du front vers l'API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Origine autorisée : le front Angular en local
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        // Authorization = header où on envoie le JWT (Bearer ...)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

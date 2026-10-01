package com.example.kanban_api.security;

import com.example.kanban_api.model.User;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/**
 * Service qui crée et lit les tokens JWT.
 *
 * Équivalents :
 * - Symfony : lexik/jwt-authentication-bundle (JWTManager)
 * - NestJS : @nestjs/jwt (JwtService.sign / verify)
 *
 * Un JWT = "carte d'identité" signée. On y met l'id user + le rôle,
 * JAMAIS le mot de passe.
 */
@Service
public class JwtService {

    private final JwtEncoder encoder; // pour créer un token
    private final JwtDecoder decoder; // pour lire / vérifier un token
    private final Duration expiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") Duration expiration
    ) {
        // Clé secrète lue depuis application.yml (app.jwt.secret)
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        this.decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        this.expiration = expiration;
    }

    /** Crée un JWT pour un user connecté (appelé au login). */
    public String generate(User user) {
        Instant now = Instant.now();
        // "claims" = infos stockées dans le token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString()) // sub = id de l'utilisateur
                .issuedAt(now)                    // date de création
                .expiresAt(now.plus(expiration))  // date d'expiration
                .claim("role", user.getRole().name()) // rôle custom (user / admin)
                .build();
        // Signature HS256 (= même algo que souvent en Nest/Symfony)
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /** Vérifie la signature + expiration, puis renvoie le contenu du token. */
    public Jwt decode(String token) {
        return decoder.decode(token);
    }
}

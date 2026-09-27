package com.example.kanban_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// Déclare le bouton Authorize de Swagger, ce n'est pas la sécurité réelle : elle est dans SecurityConfiguration
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearer = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", bearer))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public OpenApiCustomizer publicAuthRoutes() {
        return openApi -> openApi.getPaths().forEach((path, item) -> {
            if ("/api/auth/register".equals(path) || "/api/auth/login".equals(path)) {
                item.readOperations().forEach(operation -> operation.setSecurity(List.of()));
            }
        });
    }
}

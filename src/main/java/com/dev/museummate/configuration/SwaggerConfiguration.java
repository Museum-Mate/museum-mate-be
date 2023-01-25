package com.dev.museummate.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Swagger springdoc-ui 설정
 */
@OpenAPIDefinition(
        info = @Info(
                title = "MuseumMate API 명세서",
                description = "MuseumMate API",
                version = "1.0.0"
        )
)
@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi museummateOpenApi() {

        String paths[] = {"/api/**"};

        return GroupedOpenApi.builder()
                .group("MuseumMate OpenAPI 1.0.0")
                .pathsToMatch(paths)
                .build();
    }
}

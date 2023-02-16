package com.dev.museummate.global.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Swagger springdoc-ui 설정
 */
@Configuration
public class SwaggerConfiguration {

    String defaultHeader = "Authorization";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                      .title(" We Are MuseumMate 😎 ")
                      .description(" 👫 우리는 연결합니다. 작가, 작품, 그리고 당신을 👫 ")
                      .version(" 1.0.0 ")
            )
            .addSecurityItem(new SecurityRequirement().addList(defaultHeader))
            .components(new Components().addSecuritySchemes(defaultHeader,
                                                            new io.swagger.v3.oas.models.security.SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .in(SecurityScheme.In.HEADER)
                                                                .name(defaultHeader)));
    }

    @Bean
    public GroupedOpenApi museummateOpenApi() {

        String paths[] = {"/api/**"};

        return GroupedOpenApi.builder()
            .group("MuseumMate OpenAPI 1.0.0")
            .pathsToMatch(paths)
            .build();
    }
}
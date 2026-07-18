package com.pm.bankcards.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankcardsOpenAPI() {
        var appTitle = "Bankcards API";
        var appVersion = "v1";

        var appDescription = """
                Card management: CRUD, transfers, filters/pagination, JWT, I/O high load.
                """;

        var devURL = "http://localhost:8080";
        var productionURL = "https://bankcards.com";

        return new OpenAPI().info(new Info()
                        .title(appTitle)
                        .description(appDescription)
                        .version(appVersion))
                .servers(List.of(
                        new Server().url(devURL).description("Dev"),
                        new Server().url(productionURL).description("Production")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                );
    }
}

package com.memplas.parking.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Car parking API Docs", version = "1.0.0"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    @Bean
    @Profile("prod")
    public OpenAPI prodOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("https://parking-api.quenchx.com/api")
                                .description("Production API")
                ));
    }

    @Bean
    @Profile("!prod") // Not prod: dev, test, default, etc.
    public OpenAPI nonProdOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Dev API")
                ));
    }
}
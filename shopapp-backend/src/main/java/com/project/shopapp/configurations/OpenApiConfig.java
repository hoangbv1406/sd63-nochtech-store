package com.project.shopapp.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.service.server-url:http://localhost:8088}")
    private String serverUrl;

    @Value("${openapi.service.server-description:Local ENV}")
    private String serverDescription;

    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("OpenApi specification - NochTech Store")
                        .description("OpenApi documentation for NochTech Store eCommerce Application")
                        .version("1.0")
                        .contact(new Contact()
                                .name("NochTech Store")
                                .email("support@nochtech.com")
                                .url("https://nochtech.com"))
                        .license(new License().name("Licence name").url("https://some-url.com"))
                        .termsOfService("Terms of service")
                )
                .servers(List.of(new Server().url(serverUrl).description(serverDescription)))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT auth description")
                                .in(SecurityScheme.In.HEADER)
                        )
                );
    }
}

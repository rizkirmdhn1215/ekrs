package com.bandung.ekrs.config;

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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.ip")
    private String ServerIp;

    @Bean
    public OpenAPI openAPI() {
        List<Server> servers = new ArrayList<>();
        
        servers.add(new Server()
            .url(ServerIp)
            .description("Development Server"));

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .servers(servers)
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .security(List.of(new SecurityRequirement().addList("bearerAuth")))
                .info(new Info()
                        .title("E-KRS API Documentation")
                        .description("API Documentation for E-KRS System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sukvi Imoet")
                                .email("SukviImoet@jomok.com")
                                .url("https://jomok48.com"))
                        .license(new License()
                                .name("License Name")
                                .url("https://example.com")));
    }
} 
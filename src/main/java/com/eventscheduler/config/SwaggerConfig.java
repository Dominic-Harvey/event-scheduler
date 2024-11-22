package com.eventscheduler.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Event Scheduler API")
                        .version("0.0.1")
                        .description("API for managing and scheduling events without conflicts")
                        .contact(new Contact()
                                .name("Dominic Harvey")
                                .email("dominiceharvey@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ));
    }
}
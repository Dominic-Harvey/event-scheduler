package com.eventscheduler.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customObjectMapper() {
        return builder -> builder.modules(new JavaTimeModule());
    }
}

package com.example.demo.config

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun springShopOpenAPI(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("Demo")
            .pathsToMatch("/**")
            .build();
}
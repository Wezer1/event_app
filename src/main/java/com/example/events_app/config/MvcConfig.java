package com.example.events_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Для доступа через URL /uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///D:/Projects/Java/event_app/src/main/resources/")
                .setCachePeriod(3600);
    }
}
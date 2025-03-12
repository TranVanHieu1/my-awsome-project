package com.ojt.mockproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:8080",
                        "http://localhost:8888",
                        "http://localhost:8081",
                        "http://localhost:8000",
                        "http://isolutions.top:8000",
                        "http://isolutions.top:8080")
                .allowedHeaders("*")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Methods","Access-Control-Allow-Headers")
                .allowedMethods("*")
                .maxAge(1440000);
    }
}

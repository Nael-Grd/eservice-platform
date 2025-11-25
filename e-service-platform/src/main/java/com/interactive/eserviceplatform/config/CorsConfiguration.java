package com.interactive.eserviceplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure la politique CORS pour permettre au frontend React (port 5173)
 * d'accéder à l'API Spring Boot (port 8080).
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Mappage sur toutes les URL de votre API
        registry.addMapping("/api/v1/requests/**") 
                // Les adresses autorisées pour le frontend Vite/React
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

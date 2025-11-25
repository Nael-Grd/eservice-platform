package com.interactive.eserviceplatform.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Gère les erreurs d'authentification pour les requêtes non autorisées.
 * Envoie une réponse HTTP 401 (Unauthorized) formatée en JSON.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        // Journalise l'erreur d'authentification
        System.err.println("Unauthorized error: " + authException.getMessage());

        // Définit le type de contenu de la réponse comme JSON
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Code 401
        
        // Corps de la réponse JSON explicite
        String responseBody = String.format("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"%s\", \"path\": \"%s\"}",
            "Authentification requise : " + authException.getMessage(),
            request.getServletPath());
            
        response.getOutputStream().println(responseBody);
    }
}

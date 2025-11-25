package com.interactive.eserviceplatform.payload.response;

import java.util.List;

// Ceci est le DTO de réponse après un login réussi
public class AuthResponse {
    private String token;
    private String username;
    private Long id;
    private List<String> roles; 

    // Constructeur pour le résultat
    public AuthResponse(String token, Long id, String username, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    // Getters (pas besoin de setters pour une réponse)
    public String getToken() { return token; }
    public String getUsername() { return username; }
    public Long getId() { return id; }
    public List<String> getRoles() { return roles; }
}
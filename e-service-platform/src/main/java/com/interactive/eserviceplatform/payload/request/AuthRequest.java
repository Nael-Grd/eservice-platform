package com.interactive.eserviceplatform.payload.request;

// Ceci est un DTO (Data Transfer Object)
// Il représente le JSON que le client envoie pour le login ou le signup
public class AuthRequest {
    private String username;
    private String password;

    // Getters et Setters...
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
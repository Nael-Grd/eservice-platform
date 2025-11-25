package com.interactive.eserviceplatform.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.interactive.eserviceplatform.security.UserDetailsImpl; // Le service d'utilisateur que nous avons créé

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    // Injecte la clé secrète depuis application.properties
    // ATTENTION : Cette clé DOIT être sécurisée et longue (256 bits minimum)
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    // Durée de validité du jeton (en millisecondes, ex: 86400000 = 24 heures)
    @Value("${security.jwt.expiration}")
    private int jwtExpirationMs;
    
    // 1. Méthode pour générer le jeton
    public String generateJwtToken(Authentication authentication) {
        
        // Récupère les détails de l'utilisateur principal (objet standard de Spring Security)
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        
        // Récupérer les rôles de l'utilisateur pour les inclure dans le jeton
        List<String> roles = userPrincipal.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        // Clé de signature (générée à partir de la clé secrète dans application.properties)
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Construction et signature du jeton JWT
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Sujet : le nom de l'utilisateur
                .claim("roles", roles) // Ajout des rôles dans les claims
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512) // Signature avec la clé secrète et l'algorithme HS512
                .compact();
    }
    
    // --- Les méthodes de validation (nécessaires pour le filtre) ---
    
    // Méthode pour extraire le nom d'utilisateur du jeton
    public String getUserNameFromJwtToken(String token) {
        // Parse le jeton et retourne le sujet (qui est le nom d'utilisateur)
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build()
            .parseClaimsJws(token).getBody().getSubject();
    }

    // Méthode pour valider l'intégrité du jeton (signature, expiration)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            // Loggez l'erreur pour le débogage (signature invalide, jeton expiré, etc.)
            System.err.println("JWT Validation Error: " + e.getMessage());
        }
        return false;
    }
}
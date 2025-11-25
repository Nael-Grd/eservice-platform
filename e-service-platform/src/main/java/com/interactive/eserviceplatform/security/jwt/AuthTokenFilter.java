package com.interactive.eserviceplatform.security.jwt;

import com.interactive.eserviceplatform.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Méthode pour extraire le jeton de l'en-tête Authorization
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Retirer "Bearer "
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // 1. Tente d'extraire le jeton de la requête
            String jwt = parseJwt(request);

            // 2. Si le jeton existe et est valide :
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                
                // a. Récupérer le nom d'utilisateur
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // b. Charger les détails de l'utilisateur et de ses rôles (via le service)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // c. Créer l'objet d'authentification pour la requête
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // d. Charger l'objet d'authentification dans le SecurityContextHolder (C'est la magie!)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Loggez l'erreur et laissez la requête continuer non authentifiée
            System.err.println("Cannot set user authentication: " + e.getMessage());
        }

        // Laisse la requête passer au filtre suivant (ou au Controller)
        filterChain.doFilter(request, response);
    }
}
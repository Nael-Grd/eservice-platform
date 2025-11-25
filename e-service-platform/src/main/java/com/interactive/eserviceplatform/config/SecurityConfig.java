package com.interactive.eserviceplatform.config;

import com.interactive.eserviceplatform.security.UserDetailsServiceImpl;
import com.interactive.eserviceplatform.security.jwt.AuthTokenFilter;
import com.interactive.eserviceplatform.security.jwt.AuthEntryPointJwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Classe de configuration principale pour Spring Security.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true) 
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    
    // Injection par constructeur (méthode préférée de Spring)
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }
    
    // --- 1. Beans de base ---

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Gère l'authentification. Utilisé dans AuthController.
     * Cette méthode est correcte et non dépréciée.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    // --- 2. Configuration de la Chaîne de Filtres (Règles d'accès) ---

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Désactive CSRF pour les APIs REST
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Autorise l'accès à /api/auth (login, signup)
                .requestMatchers("/api/public/**").permitAll() 
                .anyRequest().authenticated() 
            );

        // NOTE CLÉ : Le DaoAuthenticationProvider est implicitement utilisé 
        // par l'AuthenticationManager qui utilise les Beans UserDetailsService et PasswordEncoder.
        // Il n'est PAS nécessaire d'ajouter explicitement http.authenticationProvider(authenticationProvider());
        // pour cette configuration basique.
        
        // Ajout du filtre JWT avant le filtre standard d'authentification
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        // Configuration CORS (autorise le frontend Vite/React)
        http.cors(cors -> cors.configure(http));
        
        return http.build();
    }

    // Configuration CORS
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("http://localhost:5173") 
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
package com.interactive.eserviceplatform.controller;

import com.interactive.eserviceplatform.model.Role;
import com.interactive.eserviceplatform.model.User;
import com.interactive.eserviceplatform.payload.request.AuthRequest;
import com.interactive.eserviceplatform.payload.response.AuthResponse;
import com.interactive.eserviceplatform.repository.RoleRepository;
import com.interactive.eserviceplatform.repository.UserRepository;
import com.interactive.eserviceplatform.security.UserDetailsImpl;
import com.interactive.eserviceplatform.security.jwt.JwtUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth") // Endpoint d'authentification : /api/auth/login, /api/auth/signup
public class AuthController {

    // Injection des dépendances nécessaires
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    // --- 1. SIGNUP (Création de compte) ---
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest signUpRequest) {
        // Tâche 1 : Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>("Error: Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Tâche 2 : Créer l'objet User et hacher le mot de passe
        User user = new User(signUpRequest.getUsername(), 
                             encoder.encode(signUpRequest.getPassword()));

        // Tâche 3 : Assigner le rôle par défaut (ROLE_USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role not found. Please ensure ROLE_USER is in the database."));
        
        user.setRoles(Collections.singleton(userRole));
        
        // Tâche 4 : Sauvegarder et retourner un message de succès
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // --- 2. LOGIN (Connexion et génération du JWT) ---
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {
        // Tâche 1 : Authentifier l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Mettre l'objet Authentication dans le SecurityContext (pour la session courante)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Tâche 2 : Générer le JWT
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // Tâche 3 : Récupérer les détails de l'utilisateur après authentification
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Tâche 4 : Construire la liste des rôles pour la réponse
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        // Tâche 5 : Retourner l'AuthResponse
        return ResponseEntity.ok(new AuthResponse(
                jwt, 
                userDetails.getId(), 
                userDetails.getUsername(), 
                roles));
    }
}

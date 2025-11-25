// Dans src/main/java/com/interactive/eserviceplatform/config/DataInitializer.java

package com.interactive.eserviceplatform.config;

import com.interactive.eserviceplatform.model.Role;
import com.interactive.eserviceplatform.model.User;
import com.interactive.eserviceplatform.repository.RoleRepository;
import com.interactive.eserviceplatform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

/**
 * Initializes base data (Roles and default Admin account) on application startup.
 * Ensures necessary roles exist for the application to function securely.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            
            // --- 1. Role Initialization ---
            // Finds or creates ROLE_USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                                          .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            System.out.println("Role ROLE_USER verified/created.");

            // Finds or creates ROLE_ADMIN
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                                           .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
            System.out.println("Role ROLE_ADMIN verified/created.");
            
            // --- 2. Default ADMIN Account Creation ---
            // Creates the default Admin account only if it does not exist.
            if (userRepository.findByUsername("admin").isEmpty()) {
                
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("adminpass")); // Hashing the password immediately
                
                // Assign the Admin role
                admin.setRoles(Collections.singleton(adminRole));
                userRepository.save(admin);
                
                System.out.println("Default Admin user created with username 'admin' and password 'adminpass'.");
            }

            // --- 3. Example Regular User (Optional but useful for testing) ---
            if (userRepository.findByUsername("user1").isEmpty()) {
                User regularUser = new User();
                regularUser.setUsername("user1");
                regularUser.setPassword(passwordEncoder.encode("userpass"));
                regularUser.setRoles(Collections.singleton(userRole));
                userRepository.save(regularUser);
                
                System.out.println("Default User 'user1' created with password 'userpass'.");
            }
        };
    }
}



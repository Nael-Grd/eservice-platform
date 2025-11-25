package com.interactive.eserviceplatform.repository;

import com.interactive.eserviceplatform.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Méthode Query Method essentielle pour le Signup :
    // Permet de trouver un rôle par son nom (ex: "ROLE_USER" ou "ROLE_ADMIN")
    Optional<Role> findByName(String name);
}
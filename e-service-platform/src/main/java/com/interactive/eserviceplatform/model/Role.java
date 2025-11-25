// Dans src/main/java/com/interactive/eserviceplatform/model/Role.java

package com.interactive.eserviceplatform.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ex: ROLE_USER, ROLE_ADMIN

    // Constructeur par défaut (OBLIGATOIRE pour JPA)
    public Role() {}

    // *** CORRECTION (TÂCHE 1) ***
    // Constructeur avec argument (Nécessaire pour DataInitializer)
    public Role(String name) {
        this.name = name;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
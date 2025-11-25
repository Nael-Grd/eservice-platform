package com.interactive.eserviceplatform.repository;

import com.interactive.eserviceplatform.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    
    // Spring Data JPA génère automatiquement toutes les méthodes CRUD de base
    // (save, findById, findAll, delete) simplement en étendant JpaRepository.

    // Nous pouvons aussi ajouter des méthodes spécifiques ici.
    // Par exemple, pour lister toutes les demandes d'un utilisateur :
    List<Request> findAllByUserId(Long userId);

    List<Request> findAllByStatus(String stauts);
}
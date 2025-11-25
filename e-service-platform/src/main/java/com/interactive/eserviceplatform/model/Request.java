
package com.interactive.eserviceplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "service_requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // L'utilisateur qui fait la demande

    private String documentType; // (CNI, PASSEPORT, PERMIS)

    private String title;
    
    private String description;

    private LocalDate birthDate;

    private String birthPlace;

    private String status;  // (DRAFT, SUBMITTED, APPROVED, etc.)
    
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime deadline;

    // --- CONSTRUCTEUR PAR DÉFAUT (nécessaire pour JPA) ---
    public Request() {
    }

    // --- GETTERS & SETTERS
    // (VS Code/IntelliJ a des raccourcis pour les générer : Alt+Insert ou Cmd+N)

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
     
    public String getDocumentType() {
        return documentType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    
}
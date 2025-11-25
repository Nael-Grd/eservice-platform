package com.interactive.eserviceplatform.exception;

// Exception personnalisée pour remplacer la RuntimeException lors d'un 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

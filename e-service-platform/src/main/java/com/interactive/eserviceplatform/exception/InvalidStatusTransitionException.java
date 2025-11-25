package com.interactive.eserviceplatform.exception;

// Exception personnalisée pour remplacer l'IllegalArgumentException lors d'un 400 lié au BPM
public class InvalidStatusTransitionException extends IllegalArgumentException {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}

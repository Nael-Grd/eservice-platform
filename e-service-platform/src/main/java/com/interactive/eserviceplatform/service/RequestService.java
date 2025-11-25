package com.interactive.eserviceplatform.service;

import com.interactive.eserviceplatform.exception.InvalidStatusTransitionException;
import com.interactive.eserviceplatform.exception.ResourceNotFoundException;
import com.interactive.eserviceplatform.model.Request;
import com.interactive.eserviceplatform.repository.RequestRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    //@Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    // 1. Création (logique simple)
    public Request createRequest(Request newRequest) {
        newRequest.setStatus("DRAFT");
        newRequest.setCreatedAt(LocalDateTime.now());
        return requestRepository.save(newRequest); 
    }
    
    // 2. Consultation par ID
    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    } 
    
    // 3. Consultation par UserId (utilise la Query Method du Repository)
    public List<Request> getRequestsByUserId(Long userId) {
        return requestRepository.findAllByUserId(userId);
    }

    // 3bis. Consultation par Statut
    public List<Request> getRequestsByStatus(String status) {
        return requestRepository.findAllByStatus(status);
    }

    // 4. Logique pour passer une demande en statut SOUMIS 
    public Request submitRequest(Long id) {
        Request request = requestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id)); 
        
        String status = request.getStatus();
        if (!"DRAFT".equals(status)) {
            throw new InvalidStatusTransitionException("Only DRAFT requests can be submitted.");
        }     
        request.setStatus("SUBMITTED");
        return requestRepository.save(request);
    }

    // 5. REJETER UNE DEMANDE (BPM - SUBMITTED -> REJECTED) 
    public Request rejectRequest(Long id) {
        Request request = requestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id));

        String status = request.getStatus();
        if (!"SUBMITTED".equals(status)) { 
            throw new InvalidStatusTransitionException("Only SUBMITTED requests can be rejected.");
        }
        request.setStatus("REJECTED");
        return requestRepository.save(request);
    }

    // 6. APPROUVER UNE DEMANDE (SUBMITTED -> REJECTED)
    public Request approveRequest(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id));
        
        String status = request.getStatus();
        if (!"SUBMITTED".equals(status)) {
            throw new InvalidStatusTransitionException("Only SUBMITTED requests can be approved.");
        }
        request.setStatus("APPROVED");
        return requestRepository.save(request);
    }
}

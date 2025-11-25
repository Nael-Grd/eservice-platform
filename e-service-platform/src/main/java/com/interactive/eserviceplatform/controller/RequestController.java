package com.interactive.eserviceplatform.controller;

import com.interactive.eserviceplatform.model.Request;
import com.interactive.eserviceplatform.service.RequestService;
import com.interactive.eserviceplatform.exception.ResourceNotFoundException; // Nécessaire pour le GET

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    private final RequestService requestService;

    // Injection de dépendance par constructeur (sans @Autowired)
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    // 1. Endpoint POST (Création)
    @PostMapping
    public ResponseEntity<Request> createRequest(@RequestBody Request request) {
        Request createdRequest = requestService.createRequest(request);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }
    
    // 2. Endpoint GET (Consultation par ID)
    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        // Si la demande n'existe pas, ResourceNotFoundException est lancée et interceptée globalement
        Request request = requestService.getRequestById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + id));
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    // 3. Endpoint GET pour lister les demandes d'un utilisateur
    // Retourne 200 OK (même si la liste est vide)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Request>> getRequestsByUserId(@PathVariable Long userId) {
        List<Request> requests = requestService.getRequestsByUserId(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    // 3bis. lister par statut
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Request>> getRequestsByStatus(@PathVariable String status) {
        List<Request> requests = requestService.getRequestsByStatus(status);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    // 4. Endpoint PUT (Soumission de la Demande)
    // Ne contient plus de try-catch : les exceptions (400, 404) sont gérées par le RestExceptionHandler
    @PutMapping("/{id}/submit")
    public ResponseEntity<Request> submitRequest(@PathVariable Long id) {
        // Laisse le service lever InvalidStatusTransitionException (-> 400) ou ResourceNotFoundException (-> 404)
        Request submittedRequest = requestService.submitRequest(id);
        return new ResponseEntity<>(submittedRequest, HttpStatus.OK);
    }
    
    // 5. Endpoint PUT (Rejet de la Demande)
    // Ne contient plus de try-catch : les exceptions (400, 404) sont gérées par le RestExceptionHandler
    @PutMapping("/{id}/reject")
    public ResponseEntity<Request> rejectRequest(@PathVariable Long id) {
        // Laisse le service lever les exceptions, retourne 200 OK en cas de succès.
        Request rejectedRequest = requestService.rejectRequest(id); 
        return new ResponseEntity<>(rejectedRequest, HttpStatus.OK);
    }

    // 6. Endpoint PUT (Approbation de la Demande)
    @PutMapping("/{id}/approve")
    public ResponseEntity<Request> approveRequest(@PathVariable Long id) {
        Request approvedRequest = requestService.approveRequest(id);
        return new ResponseEntity<>(approvedRequest, HttpStatus.OK);
    }
}

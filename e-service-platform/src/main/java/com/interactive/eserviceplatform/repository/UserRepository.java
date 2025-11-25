package com.interactive.eserviceplatform.repository;

import com.interactive.eserviceplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
 
    // Cette Query Method est cruciale pour Spring Security :
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    
}
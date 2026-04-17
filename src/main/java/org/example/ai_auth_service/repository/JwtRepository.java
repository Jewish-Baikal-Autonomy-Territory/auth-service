package org.example.ai_auth_service.repository;

import org.example.ai_auth_service.entity.JWT;
import org.example.ai_auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JwtRepository extends JpaRepository<JWT, UUID> {
    Optional<JWT> findByToken (String token);
    Optional<JWT> findByUser (User user);
    void deleteByToken(String token);
}

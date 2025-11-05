package com.maitrunghau.hotelbookingsystem.repository;

import com.maitrunghau.hotelbookingsystem.model.AuthToken;
import com.maitrunghau.hotelbookingsystem.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByTokenAndType(String token, TokenType type);
    Optional<AuthToken> findByToken(String token);
}

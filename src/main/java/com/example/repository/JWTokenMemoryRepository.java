package com.example.repository;

import com.example.entity.JWToken;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class JWTokenMemoryRepository {

    private static final Map<String, JWToken> db = new HashMap<>();

    public JWToken save(JWToken jwToken) {
        db.put(jwToken.getUsername(), jwToken);
        return jwToken;
    }

    public Optional<JWToken> findByUsername(String username) {
        return Optional.ofNullable(db.get(username));
    }
}

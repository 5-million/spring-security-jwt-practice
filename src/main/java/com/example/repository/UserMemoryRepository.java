package com.example.repository;

import com.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserMemoryRepository {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Map<String, User> db = new HashMap<>();

    public Optional<User> findById(String id) {
        return Optional.ofNullable(db.get(id));
    }

    public void save(User user) {
        final String id = user.getUsername();
        if (db.containsKey(id)) log.warn("user save fail: {} is already exists.", id);
        else {
            db.put(id, user);
            log.info("{} save success.", id);
        }
    }
}

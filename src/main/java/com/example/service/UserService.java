package com.example.service;

import com.example.entity.User;
import com.example.exception.UserNotFoundException;
import com.example.repository.UserMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMemoryRepository userMemoryRepository;
    private final PasswordEncoder passwordEncoder;

    public void join(User user) {
        user.encryptPassword(passwordEncoder);
        userMemoryRepository.save(user);
    }

    public User getById(String id) {
        return userMemoryRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("%s is not registered.", id)));
    }
}

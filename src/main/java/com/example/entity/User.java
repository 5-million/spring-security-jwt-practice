package com.example.entity;

import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class User {

    private String username;
    private String password;
    private Role role = Role.ROLE_USER;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}

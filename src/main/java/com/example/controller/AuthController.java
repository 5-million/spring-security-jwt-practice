package com.example.controller;

import com.example.dto.JWTokenRequest;
import com.example.dto.LoginRequest;
import com.example.entity.JWToken;
import com.example.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<JWToken> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/auth/token/reissue")
    public ResponseEntity<JWToken> reissue(@RequestBody JWTokenRequest request) {
        return ResponseEntity.ok(authService.reissue(request));
    }
}

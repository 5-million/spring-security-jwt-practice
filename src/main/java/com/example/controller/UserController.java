package com.example.controller;

import com.example.dto.JoinRequest;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;

    @PostMapping("/auth/join")
    public void join(@RequestBody JoinRequest request) {
        userService.join(request.toEntity());
    }

    @GetMapping("/me")
    public ResponseEntity<JoinRequest> me(Principal principal) {
        log.info(principal.getName());
        return ResponseEntity.ok(JoinRequest.of(userService.getById(principal.getName())));
    }
}

package com.example.controller;

import com.example.dto.JoinRequest;
import com.example.dto.UserInfo;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/join")
    public void join(@RequestBody JoinRequest request) {
        userService.join(request.toEntity());
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> me(Principal principal) {
        log.info(principal.getName());
        return ResponseEntity.ok(UserInfo.of(userService.getById(principal.getName())));
    }

    @GetMapping("/admin/me")
    public ResponseEntity<UserInfo> adminMe(Principal principal) {
        log.info(principal.getName());
        return ResponseEntity.ok(UserInfo.of(userService.getById(principal.getName())));
    }
}

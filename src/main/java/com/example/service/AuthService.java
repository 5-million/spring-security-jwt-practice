package com.example.service;

import com.example.dto.JWTokenRequest;
import com.example.dto.LoginRequest;
import com.example.entity.JWToken;
import com.example.exception.IllegalTokenException;
import com.example.jwt.JwtProvider;
import com.example.jwt.TokenType;
import com.example.repository.JWTokenMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final JWTokenMemoryRepository JWTokenMemoryRepository;

    public JWToken login(LoginRequest request) {
        String username = request.getUsername();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JWToken jwToken = jwtProvider.issueToken(authentication);
        JWTokenMemoryRepository.save(jwToken);

        return jwToken;
    }

    public JWToken reissue(JWTokenRequest request) {
        jwtProvider.validateToken(request.getRefreshToken(), TokenType.REFRESH_TOKEN);

        Authentication authentication = jwtProvider.getAuthentication(request.getAccessToken());
        JWToken oldJWToken = JWTokenMemoryRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalTokenException("not found refresh token"));

        if (!oldJWToken.getRefreshToken().equals(request.getRefreshToken())) {
            throw new IllegalTokenException("illegal refresh token");
        }

        JWToken newJWToken = jwtProvider.issueToken(authentication);
        oldJWToken.update(newJWToken);

        return newJWToken;
    }
}

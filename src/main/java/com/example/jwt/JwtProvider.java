package com.example.jwt;

import com.example.entity.JWToken;
import com.example.exception.CustomJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class JwtProvider {

    private static final String AUTH_CLAIM_NAME = "auth";
    private static final long accessTokenExpiration = 1000 * 60 * 30; // 30분
    private static final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 15; // 15일

    private final Key accessTokenKey;
    private final Key refreshTokenKey;

    public JwtProvider(
            @Value("${jwt.secret.accessToken}") String accessTokenSecret,
            @Value("${jwt.secret.refreshToken}") String refreshTokenSecret) {
        accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes());
        refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes());
    }

    public JWToken issueToken(Authentication authentication) {
        String accessToken = generateToken(authentication, accessTokenExpiration, accessTokenKey);
        String refreshToken = generateToken(authentication, refreshTokenExpiration, refreshTokenKey);
        return new JWToken(authentication.getName(), accessToken, refreshToken);
    }

    private String generateToken(Authentication authentication, long expiration, Key key) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        return Jwts.builder()
                .setHeader(buildHeader())
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(now))
                .claim(AUTH_CLAIM_NAME, authorities)
                .setExpiration(new Date(now + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> buildHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        return header;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTH_CLAIM_NAME).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal,"", authorities);
    }

    public void validateToken(String token, TokenType tokenType) {
        if (tokenType == TokenType.ACCESS_TOKEN) validateToken(token, accessTokenKey);
        else validateToken(token, refreshTokenKey);
    }

    private void validateToken(String token, Key tokenKey) {
        try {
            Jwts.parserBuilder().setSigningKey(tokenKey).build().parseClaimsJws(token);
        } catch (JwtException e) {
            String message;

            if (e instanceof SignatureException || e instanceof MalformedJwtException) message = "잘못된 JWT 서명입니다.";
            else if (e instanceof ExpiredJwtException) message = "만료된 JWT 토큰입니다.";
            else if (e instanceof UnsupportedJwtException) message = "지원되지 않는 JWT 토큰입니다.";
            else message = "JWT 토큰이 잘못되었습니다.";

            throw new CustomJwtException(message);
        }
    }

    private Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(accessToken).getBody();
    }
}

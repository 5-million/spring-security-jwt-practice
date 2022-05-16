package com.example.jwt;

import com.example.entity.JWToken;
import com.example.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Key accessTokenKey;
    private final Key refreshTokenKey;
    private final long accessTokenExpiration = 1000 * 60 * 30; // 30분
    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 15; // 15일

    public JwtProvider() {
//        accessTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("accessToken"));
//        refreshTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("refreshToken"));
        accessTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        refreshTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
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
                .claim("auth", authorities)
                .setExpiration(new Date(now + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> buildHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");

        return header;
    }

    private Map<String, Object> buildClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("auth", user.getRole());

        return claims;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal,"", authorities);
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(accessToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshTokenKey).build().parseClaimsJws(refreshToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(accessToken).getBody();
    }
}

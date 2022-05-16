package com.example.dto;

public class JWTokenRequest {

    private String accessToken;
    private String refreshToken;

    public JWTokenRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static JWTokenRequest of(String[] tokens) {
        return new JWTokenRequest(tokens[0], tokens[1]);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

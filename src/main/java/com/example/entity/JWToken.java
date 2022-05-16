package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JWToken {

    private String username;
    private String accessToken;
    private String refreshToken;

    public void update(JWToken newJWToken) {
        this.accessToken = newJWToken.getAccessToken();
        this.refreshToken = newJWToken.getRefreshToken();
    }
}

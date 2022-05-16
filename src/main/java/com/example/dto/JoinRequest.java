package com.example.dto;

import com.example.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequest {

    private String username;
    private String password;

    public static JoinRequest of(User user) {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.username = user.getUsername();
        joinRequest.password = user.getPassword();
        return joinRequest;
    }

    public User toEntity() {
        return new User(this.username, this.password);
    }
}

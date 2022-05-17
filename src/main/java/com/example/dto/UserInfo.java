package com.example.dto;

import com.example.entity.Role;
import com.example.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {

    private String username;
    private String password;
    private Role role;

    public static UserInfo of(User user) {
        return new UserInfo(user.getUsername(), user.getPassword(), user.getRole());
    }
}

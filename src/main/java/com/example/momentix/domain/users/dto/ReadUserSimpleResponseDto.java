package com.example.momentix.domain.users.dto;

import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.users.entity.Users;

public class ReadUserSimpleResponseDto {
    private final Long userId;
    private final String username;
    private final String nickname;
    private final String phoneNumber;
    private final String role;

    public ReadUserSimpleResponseDto(
            Long userId,
            String username,
            String nickname,
            String phoneNumber,
            String role
    ) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public static ReadUserSimpleResponseDto from(Users users) {
        SignIn signIn = users.getSignIn();
        return new ReadUserSimpleResponseDto(
                users.getUserId(),
                signIn != null ? signIn.getUsername() : null,
                users.getNickname(),
                users.getPhoneNumber(),
                users.getRole().name()
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }
}

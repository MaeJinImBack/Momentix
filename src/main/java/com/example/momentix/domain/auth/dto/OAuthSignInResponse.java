package com.example.momentix.domain.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthSignInResponse {
    private final String access_token;
    private final String refresh_token;
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String provider;
}

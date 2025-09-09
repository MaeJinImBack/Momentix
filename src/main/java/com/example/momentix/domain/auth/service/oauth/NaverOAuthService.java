package com.example.momentix.domain.auth.service.oauth;


import com.example.momentix.domain.auth.dto.OAuthSignInResponse;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.auth.util.OAuthClient;
import com.example.momentix.domain.users.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverOAuthService implements OAuthService{
    private static final String TOKEN_URL="https://nid.naver.com/oauth2.0/token";
    private static final String AUTHORIZE_URL="https://openapi.naver.com/v1/nid/me";

    private final OAuthClient oAuthClient;
    private final UserRepository userRepository;
    private final SignInRepository signInRepository;
    private final ObjectMapper objectMapper;


    @org.springframework.beans.factory.annotation.Value("${naver.client.id}")
    private String clientId;
    @org.springframework.beans.factory.annotation.Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.state}")
    private String state;

    @Override
    public OAuthSignInResponse signIn(String code, String state) {
        return null;
    }
}

package com.example.momentix.domain.auth.service.oauth;

import com.example.momentix.domain.auth.dto.OAuthSignInResponse;
import com.example.momentix.domain.auth.entity.RoleType;
import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.auth.util.OAuthClient;
import com.example.momentix.domain.common.util.JwtUtil;
import com.example.momentix.domain.users.entity.Users;
import com.example.momentix.domain.users.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KaKaoOAuthService implements OAuthService {

    private static final String TOKEN_URL   = "https://kauth.kakao.com/oauth/token";
    private static final String PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

    private final OAuthClient oAuthClient;
    private final UserRepository userRepository;
    private final SignInRepository signInRepository;
    private final ObjectMapper objectMapper;

    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.client.secret}")
    private String clientSecret;
    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Transactional
    @Override
    public OAuthSignInResponse signIn(String code, String state) {
        try {
            // 1) 토큰 요청
            String tokenResponse = oAuthClient.postForm(
                    TOKEN_URL,
                    "grant_type", "authorization_code",
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "code", code,
                    "redirect_uri", redirectUri
            );

            JsonNode tokenJson = objectMapper.readTree(tokenResponse);
            String accessToken = tokenJson.get("access_token").asText(null);
            if (accessToken == null) {
                throw new IllegalArgumentException("카카오 토큰 발급 실패: " + tokenResponse);
            }

            // 2) 프로필 조회
            String profileResponse = oAuthClient.get(PROFILE_URL, accessToken);
            JsonNode root = objectMapper.readTree(profileResponse);
            JsonNode kakaoAccount = root.path("kakao_account");

            String email = kakaoAccount.path("email").asText(null);
            String nickname = kakaoAccount.path("profile").path("nickname").asText(null);
            String profileImage = kakaoAccount.path("profile").path("profile_image_url").asText(null);

            if (email == null) {
                throw new IllegalArgumentException("카카오 계정에 이메일 제공 동의 필요");
            }

            // 3) 유저 조회/생성
            Users user = userRepository.findBySignIn_Username(email).orElseGet(() -> {
                Users newUser = new Users();
                newUser.setEmail(email);
                newUser.setNickname(nickname);
                newUser.setRole(RoleType.CONSUMER);
                return userRepository.save(newUser);
            });

            signInRepository.findByUsername(email).orElseGet(() -> {
                SignIn signIn = new SignIn();
                signIn.setUsername(email);
                signIn.setPassword("{noop}");
                signIn.setUsers(user);
                return signInRepository.save(signIn);
            });

            // 4) JWT 발급
            String accessJwt = JwtUtil.createAccessToken(user.getUserId(), email, user.getRole());
            String refreshJwt = JwtUtil.createRefreshToken(user.getUserId());

            return new OAuthSignInResponse(
                    accessJwt, refreshJwt, user.getUserId(), email, user.getNickname(), "KAKAO"
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("카카오 OAuth 처리 실패: " + e.getMessage(), e);
        }
    }
}
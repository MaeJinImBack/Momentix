package com.example.momentix.domain.auth.controller;


import com.example.momentix.domain.auth.dto.OAuthSignInResponse;
import com.example.momentix.domain.auth.entity.OAuthProvider;
import com.example.momentix.domain.auth.service.oauth.OAuthService;
import com.example.momentix.domain.auth.service.oauth.OAuthServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth/sign-in/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthServiceFactory oAuthServiceFactory;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.redirect.url}")
    private String redirectUri;

    @Value("${naver.state}")
    private String state;

    @GetMapping("/naver")
   public ResponseEntity<Void> naver(){
        String url = UriComponentsBuilder
                .fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri) // 네이버 콘솔과 완전 동일해야 함 (인코딩 주의)
                .queryParam("state", state)
                .build(false) // redirect_uri에 이미 인코딩 되어 있으면 false
                .toUriString();
        return ResponseEntity.status(302).location(URI.create(url)).build();
    }


    // auth/sign-in/oauth/callback/naver → code를 받아서 토큰 발급 요청 → 프로필 조회 → DB 저장 → JWT 리턴
    @GetMapping("/callback/naver")
    public OAuthSignInResponse callback(
            @RequestParam("code") String code,
            @RequestParam("state") String stateParam
    ) {
        OAuthService service = oAuthServiceFactory.getOAuthService(OAuthProvider.NAVER);
        return service.signIn(code, stateParam);
    }
}

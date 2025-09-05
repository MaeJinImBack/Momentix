package com.example.momentix.domain.auth.controller;

import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.common.util.JwtUtil;
import com.nimbusds.oauth2.sdk.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;


@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SignInRepository signInRepository; // userId 조회용

    @PostMapping("/sign-in")
    public ResponseEntity<TokenRes> signIn(@RequestBody LoginReq req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        String username = principal.getUsername();

        String role = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).findFirst().orElse("ROLE_USER")
                .replace("ROLE_", "");

        Long userId = signInRepository.findByUsername(username)
                .map(s -> s.getUser().getUserId())
                .orElseThrow();

        String accessToken  = JwtUtil.createAccessToken(userId, username, role);

        String refreshToken = JwtUtil.createRefreshToken(userId);

        // HttpOnly 리프레시 쿠키
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new TokenRes(accessToken, null));// 바디에만
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal UserDetails user) {
        return Map.of(
                "username", user.getUsername(),
                "roles", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }

    // 요청/응답 DTO
    public record LoginReq(String username, String password) {}
    public record TokenRes(String accessToken, String refreshToken) {}
}

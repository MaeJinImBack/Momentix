package com.example.momentix.domain.auth.service;


import com.example.momentix.domain.auth.entity.RoleType;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.common.util.JwtUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class SignInService {
    private final AuthenticationManager authenticationManager;
    private final SignInRepository signInRepository;

    public Tokens signIn(String username, String rawPassword) {
        // 400
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 아이디 또는 비밀번호입니다.");
        }

        try {
            // 인증 시도 (아이디/비번 불일치면 BadCredentialsException)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, rawPassword)
            );

            UserDetails principal = (UserDetails) authentication.getPrincipal();
            String resolvedUsername = principal.getUsername();

            RoleType role = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.replace("ROLE_", ""))
                    .map(RoleType::valueOf)
                    .findFirst()
                    .orElse(RoleType.CONSUMER);

            // 401
            Long userId = signInRepository.findUserIdByUsername(resolvedUsername)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "없는 유저입니다."));

            String accessToken = JwtUtil.createAccessToken(userId, resolvedUsername, role);
            String refreshToken = JwtUtil.createRefreshToken(userId);

            return new Tokens(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            // 401
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호 불일치합니다.");
        } catch (DisabledException | LockedException | AccountExpiredException e) {
            // 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "블랙리스트 계정 또는 탈퇴 계정입니다.");
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Tokens {
        private final String accessToken;
        private final String refreshToken;
    }
}

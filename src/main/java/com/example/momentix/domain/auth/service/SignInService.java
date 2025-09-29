package com.example.momentix.domain.auth.service;


import com.example.momentix.domain.auth.entity.RoleType;
import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.common.util.JwtUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.momentix.domain.common.exception.auth.AuthErrorException;
import static com.example.momentix.domain.common.exception.auth.AuthErrorCode.*;


@Service
@RequiredArgsConstructor
public class SignInService {
    private final AuthenticationManager authenticationManager;
    private final SignInRepository signInRepository;

    public Tokens signIn(String username, String rawPassword) {
        // 400
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new AuthErrorException(BAD_REQUEST);
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
                    .orElseThrow(() -> new AuthErrorException(NOT_FOUND));

            String accessToken = JwtUtil.createAccessToken(userId, resolvedUsername, role);
            String refreshToken = JwtUtil.createRefreshToken(userId);

            return new Tokens(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            // 401
            throw new AuthErrorException(BAD_REQUEST);
        } catch (DisabledException | LockedException | AccountExpiredException e) {
            // 403
            throw new AuthErrorException(BLACK_USER);
        }
    }

    // 리프레시 엔드포인트
    public SignIn loadByUserId(Long userId) {
        return signInRepository.findById(userId)
                .orElseThrow(() -> new AuthErrorException(NOT_FOUND));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Tokens {
        private final String accessToken;
        private final String refreshToken;
    }
}

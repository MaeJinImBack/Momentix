package com.example.momentix.domain.auth.controller;

import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.common.util.JwtUtil;
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
    public ResponseEntity<TokenRes> signIn(@RequestBody SigninReq req) {
        // 사용자가 입력한 username/password로 인증 시도
        Authentication authentication = authenticationManager.authenticate(
               // 사용자가 입력한 아이디/비번 토큰 객체에 담음
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        // 로그인 성공 시 반환된 사용자 정보 객체 (UserDetailsImpl 구현체)
         // Authentication에서 꺼내서 principal 변수에 담음
        // TODO:타입체크 후 캐스팅하는 방식임
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        // 로그인한 사용자의 아이디(username) 조회
        String username = principal.getUsername();

        // 권한 가져오기
        String role = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("ROLE_USER")
                .replace("ROLE_", "");


        // username으로 DB에서 SignIn엔티티 찾음
        Long userId = signInRepository.findByUsername(username)
                .map(s -> s.getUser().getUserId())
                .orElseThrow();


        // 로그인 성공 후 JWT 발급(추후에 삭제할 거임, 바디에 어세스 토큰 보여야 해서 일단 넣음)
        String accessToken = JwtUtil.createAccessToken(userId, username, role);
        String refreshToken = JwtUtil.createRefreshToken(userId);


        //refreshToken 생성
        // HttpOnly 리프레시 쿠키<- XSS 때문, 나중에 리프레시 엔드포인트 요청 만들 예정!
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true) //XSS 공격 방지
                .secure(true)
                .sameSite("Strict") // CSRF 방지
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7)) //유효기간 7일짜리 쿠키
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new TokenRes(accessToken, null));// 바디에만
    }

    // 로그인한 사용자의 아이디와 권한(Role)을 확인하기 위한 테스트용 엔드포인트
    //@AuthenticationPrincipal로 현재 인증된 UserDetails 객체를 받아옴
    // 회원가입 이후 지울 예정
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

    //  recode: DTO 용도로 쓰는 간단한 클래스(불변객체)
    // SigninReq: 로그인 요청(username, passeord)
    // Tokenres: 로그인 응답(어세스, 리프레시)
    public record SigninReq(String username, String password) {}
    public record TokenRes(String accessToken, String refreshToken) {}
}

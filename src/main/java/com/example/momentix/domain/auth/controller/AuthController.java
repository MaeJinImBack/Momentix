package com.example.momentix.domain.auth.controller;


import com.example.momentix.domain.auth.dto.SignUpRequest;
import com.example.momentix.domain.auth.service.SignInService;
import com.example.momentix.domain.auth.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.Map;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final SignInService signInService;
    private final SignUpService signUpService;

    @PostMapping("/sign-in")
    public ResponseEntity<TokenRes> signIn(@RequestBody SigninReq req) {
        SignInService.Tokens tokens = signInService.signIn(req.username(), req.password());
        //  리프레시 토큰을 HttpOnly 쿠키로 내려줌 (나중에 엔드포인트 만들 예정)
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(true)// 로컬 개발 http면 false로
                .sameSite("Strict")
                .path("/auth/refresh")// 추후 엔드포인트 만들 때 재사용
                .maxAge(Duration.ofDays(7))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new TokenRes(tokens.getAccessToken(), null));// 바디엔 access만
    }

    // 이후에 /auth/refresh, /auth/logout 추가 예정

    @PostMapping("/sign-up/user")
    public ResponseEntity<Map<String,String>> signUpUser( @Validated(SignUpRequest.UserSignUp.class) @RequestBody SignUpRequest req) {
        signUpService.signUpUser(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message","회원가입 성공"));
    }

    @PostMapping("/sign-up/host")
    public ResponseEntity<Map<String,String>> signUpHost(
            @Validated(SignUpRequest.HostSignUp.class)
            @RequestBody SignUpRequest req) {

        Map<String, String> creds = signUpService.signUpHost(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message",  "회원가입 성공! 아이디와 비밀번호는 일치합니다. 비밀번호를 변경해 주세요.",
                        "username", creds.get("username"),
                        "password", creds.get("password")
                ));
    }

    public record SigninReq(String username, String password) {}
    public record TokenRes(String accessToken, String refreshToken) {}
}

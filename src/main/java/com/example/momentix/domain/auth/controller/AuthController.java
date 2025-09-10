package com.example.momentix.domain.auth.controller;


import com.example.momentix.domain.auth.dto.*;
import com.example.momentix.domain.auth.service.EmailVerificationService;
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
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final SignInService signInService;
    private final SignUpService signUpService;
    private final EmailVerificationService emailVerificationService;

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

    //이메일 인증(회원가입 전 단계 - consumer)
    @PostMapping("/email-verification/sign-up")
    public ResponseEntity<Void> requestEmailCode(@RequestBody EmailVerifyRequest req) {
        emailVerificationService.sendCode(req.getEmail());
        return ResponseEntity.ok().build();
    }

    // 코드확인 및 검증 토큰 발급
    @PostMapping("/email-verification/sign-up-verify")
    public ResponseEntity<EmailVerifyConfirmResponse> confirmEmailCode(
            @RequestBody EmailVerifyConfirm req) {
        // Lombok @Getter니까 req.getEmail(), req.getCode() 로 접근
        String token = emailVerificationService.confirmAndIssueToken(req.getEmail(), req.getCode());
        // 응답 DTO도 Lombok @AllArgsConstructor 쓰니까 바로 new 가능
        return ResponseEntity.ok(new EmailVerifyConfirmResponse(token));
    }

    // 이후에 /auth/refresh, /auth/logout 추가 예정

    @PostMapping("/sign-up/user")
    public ResponseEntity<SignUpResponse> signUpUser(
            @Validated(SignUpRequest.UserSignUp.class) @RequestBody SignUpRequest req,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractBearer(authHeader);
        String email = emailVerificationService.consumerVerifiedToken(token); // Redis에서 email 복구

        Long userId = signUpService.signUpUser(email, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SignUpResponse("회원가입 성공", userId));
    }

    @PostMapping("/sign-up/host")
    public ResponseEntity<Map<String,String>> signUpHost(
            @Validated(SignUpRequest.HostSignUp.class)
            @RequestBody SignUpRequest req) {

        Map<String, String> creds = signUpService.signUpHost(req);

        Map<String, String> body = new HashMap<>();
        body.put("message",  "회원가입 성공! 아이디와 비밀번호는 일치합니다. 비밀번호를 변경해 주세요.");
        body.put("username", creds.get("username"));
        body.put("password", creds.get("password"));

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public record SigninReq(String username, String password) {}
    public record TokenRes(String accessToken, String refreshToken) {}

    private String extractBearer(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("유효하지 않은 Authorization 헤더");
    }


}

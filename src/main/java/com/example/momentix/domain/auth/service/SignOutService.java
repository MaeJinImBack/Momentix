package com.example.momentix.domain.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignOutService {
    private static final String ACCESS_TOKEN="ACCESS_TOKEN";
    private static final String   REFRESH_TOKEN="REFRESH_TOKEN";

    public void signOut(HttpServletResponse response){
        //액세스 토큰 쿠키 만료(삭제)
        ResponseCookie expiredAccess  = ResponseCookie.from(ACCESS_TOKEN, "")
                .path("/")
                .httpOnly(true)
                .secure(true)      // HTTPS 권장
                .sameSite("Strict")// CSRF 완화
                .maxAge(0)         // 즉시 만료
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccess.toString());

        //Refresh Token 쿠키 삭제
        ResponseCookie expiredRefresh = ResponseCookie.from(REFRESH_TOKEN, "")
                .path("/auth/refresh")
                .httpOnly(true)
                .secure(false)              // 로컬 개발 http면 false
                .sameSite("Strict")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefresh.toString());
    }
}

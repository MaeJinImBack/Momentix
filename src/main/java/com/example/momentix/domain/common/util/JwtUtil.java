package com.example.momentix.domain.common.util;



import com.example.momentix.domain.auth.impl.UserDetailsImpl;
import com.example.momentix.domain.auth.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

public class JwtUtil {
    private static SecretKey secretKey;

    private static final long ACCESS_TOKEN = 1000L * 60 * 30;
    private static final long REFRESH_TOKEN = 1000L * 60 * 60 * 24 * 7;

    //  초기화
    public static void init(String secret){
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    //어세스 토큰 생성
    public static String createAccessToken(Long userId, String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성
    public static String createRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject("refresh")
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료됨: " + e.getMessage());
        } catch (JwtException e) {
            System.out.println("토큰 검증 실패: " + e.getMessage());
        }
        return false;
    }

    // Claims 추출
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 이메일(subject) 꺼내기
    public static String getUserEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // role 꺼내기
    public static String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 토큰만으로 Authentication 생성 (DB 조회 X)
    public static Authentication getAuthenticationFromToken(String token,  UserDetailsService userDetailsService ) {
        String username = getUserEmailFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

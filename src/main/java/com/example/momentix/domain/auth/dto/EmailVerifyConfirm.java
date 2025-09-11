package com.example.momentix.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyConfirm {
    // 코드 검증 요청용
    private String email;
    private String code;
}
//2) 클라이언트가 서버에 이메일+인증코드 제출 (EmailVerifyConfirm)
//서버가 Redis에 저장된 코드와 비교, 일치하면 "이메일 검증 토큰(UUID)" 발급 후 Redis에 저장
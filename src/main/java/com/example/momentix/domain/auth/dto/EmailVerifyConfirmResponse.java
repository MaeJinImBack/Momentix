package com.example.momentix.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyConfirmResponse {
    // 검증 성공하고 토큰 응답용
    private String emailVerifiedToken;
}

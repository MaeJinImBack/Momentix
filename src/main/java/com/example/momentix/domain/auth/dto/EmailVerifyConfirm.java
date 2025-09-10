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

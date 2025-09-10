package com.example.momentix.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyRequest {
    //이메일 인증 코드 요청용
    private  String email;
}

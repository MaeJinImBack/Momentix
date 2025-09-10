package com.example.momentix.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.UUID;

//코드/토큰+Redis/메일 전송
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;


    @Value("${auth.email.code-ttl-sec:600}")// 인증코드 10분
    private long codeTtlSec;
    @Value("${auth.email.token-ttl-sec:900}")// 검증토큰 15분
    private long tokenTtlSec;
    @Value("${auth.email.cooldown-sec:45}")// 재전송 쿨다운 45초
    private long cooldownSec;

    private String codeKey(String email) { return "momentix:email:code:" + email; }
    private String cooldownKey(String email) { return "momentix:email:cooldown:" + email; }
    private String tokenKey(String token) { return "momentix:email:verified:" + token; }

    //인증 코드 발송
    public void sendCode(String email) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000); //6자리
        //쿨다운
        if(Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey(email)))){
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "잠시 후에 다시 시도해 주세요.");
        }
        // 코드 저장 (TTL)
        redisTemplate.opsForValue().set(codeKey(email), code, Duration.ofSeconds(codeTtlSec));
        // 쿨다운 시작
        redisTemplate.opsForValue().set(cooldownKey(email), "1", Duration.ofSeconds(cooldownSec));

        //간단 텍스트 메일 예시(운영 시 템플릿/발신자 도메인 구성 권장)
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[MOMENTIX] 이메일 인증 코드");
        message.setText("인증 코드 " + code +"\n유효시간: " +(codeTtlSec/60)+ "분");
        mailSender.send(message);
    }

    // 코드확인/ 검증 토큰(UUID) 발급
    public String confirmAndIssueToken(String email, String code) {
        String saved = redisTemplate.opsForValue().get(codeKey(email));
        if (saved == null || !saved.equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증에 실패했습니다. 코드를 다시 요청해 주세요.");
        }
        // 코드 소모(선택)
        redisTemplate.delete(codeKey(email));

        // 검증토큰 발급(짧은 TTL, 1회성)
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(tokenKey(token), email, Duration.ofSeconds(tokenTtlSec));
        return token;
    }

    //최종 가입에서 토큰 소비 -> 이메일 복구(1회성)
    public String consumerVerifiedToken(String token){
        String email=redisTemplate.opsForValue().getAndDelete(tokenKey(token));
        if(email==null||email.isBlank()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 토큰이 만료되었거나 이미 사용되었습니다.");
        }
        return email;
    }
}

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
    @Value("${auth.email.token-ttl-sec:900}")// 900=기본값
    private long tokenTtlSec;
    @Value("${auth.email.cooldown-sec:45}")// 재전송 쿨다운 45초
    private long cooldownSec;

    // 사용자가 이메일 인증코드 받을 때 저장할 redis key 생성
    private String codeKey(String email) {
        return "momentix:email:code:" + email;
    }
    // 같은 이메일에 너무 자자 요청하지 못하도록 쿨다운 시간관리햐는 redis key
    private String cooldownKey(String email) {
        return "momentix:email:cooldown:" + email;
    }
    //발급된 토큰이 실제로 인증된 상태인지 확인할 떄 쓰는 redis key
    private String tokenKey(String token) {
        return "momentix:email:verified:" + token;
    }

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

        // SimpleMailMessage: 제목, 본문, 수신자만 간단히 담는 이메일 객체
        SimpleMailMessage message = new SimpleMailMessage();
        //수신자 이메일 설정
        message.setTo(email);
        // 메일 제목
        message.setSubject("[MOMENTIX] 이메일 인증 코드");
        // 메일 본문 설정
        message.setText("인증 코드 " + code +"\n유효시간: " +(codeTtlSec/60)+ "분");
        // 실제 메일 전송(STMP서버 통해 발송)
        mailSender.send(message);
    }

    // 사용자가 제출한 이메일/코드 확인하고 인증 성공 시 1회용 검증 토큰 발생
    public String confirmAndIssueToken(String email, String code) {
        // 1. redis에서 저장된 인증 코드 조회
        String saved = redisTemplate.opsForValue().get(codeKey(email));
        // 2. 저장된 코드가 없거나 사용자가 입력한 값과 다르면 인증 실패임
        if (saved == null || !saved.equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "인증에 실패했습니다. 코드를 다시 요청해 주세요.");
        }
        // 3. 인증 코드 1회 사용 후 바로 제거, 재사용 방지
        redisTemplate.delete(codeKey(email));

        // 4. 인증 성공했으므로 새로운 검증 토큰 발급(UUID 형태, 고유 값)
        String token = UUID.randomUUID().toString();
        // 5. 발급된 토큰을 reids에 저장(유효시간 TTL 설정, 15분)
        redisTemplate.opsForValue().set(tokenKey(token), email, Duration.ofSeconds(tokenTtlSec));
        // 6. 발급된 토큰을 클라이언트에 반환
        return token;
    }

    //최종 가입에서 토큰 소비 -> 이메일 복구(1회성)
    public String consumerVerifiedToken(String token){
        // 1. 토큰 문자열을 redis key형태로 반환
        String key = tokenKey(token);
        //2. redis에서 이 토큰에 매핑된 이메일 값 조회
        String email = redisTemplate.opsForValue().get(key);
        //3. 조회된 값이 없거나 공백이면 토큰 만료되었거나 이미 사용중
        if(email==null||email.isBlank()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 토큰이 만료되었거나 이미 사용되었습니다.");
        }
        return email;
    }
}

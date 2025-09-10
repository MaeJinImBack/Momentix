package com.example.momentix.domain.auth.service;

import com.example.momentix.domain.auth.dto.SignUpRequest;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.users.entity.Users;
import com.example.momentix.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final SignInRepository signInRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String HOST_PREFIX = "momentixHost";

    @Transactional
    public Long signUpUser(String email, SignUpRequest req) {
        if(!req.getPassword().equals(req.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다.");
        }
        if(signInRepository.existsByUsername(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 가입된 이메일입니다.");
        }

        Users users = Users.createConsumer(email, req,passwordEncoder);
        userRepository.save(users);
        return users.getUserId();
    }
    
    // 일반 유저 회원가입
//    @Transactional
//    public Long signUpUser(SignUpRequest signUpRequest) {
//
//        // 400: 비밀번호 확인 로직
//        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청(비밀번호 불일치)");
//        }
//        // 409: 아이디 중복 확인
//        if (signInRepository.existsByUsername(signUpRequest.getUsername())) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다");
//        }
//
//        // DTO → Entity 변환 과정
//        Users users = Users.createConsumer(signUpRequest, passwordEncoder);
//        userRepository.save(users);
//
//        // 저장
//        return users.getUserId();
//    }

    //호스트 가입(입력은 사업자번호 하나, username/password는 자동 생성 & 동일)
    @Transactional
    public Map<String, String> signUpHost(SignUpRequest signUpRequest) {
        // 409: 사업자번호 중복 확인
        if (userRepository.existsByBusinessNumber(signUpRequest.getBusinessNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 사업자 번호입니다.");
        }

        String username = nextHostUsername(); // ex) momentixHost0001!
        String rawPassword = username;        // 아이디와 비밀번호 동일

        // User + SignIn 객체 생성 및 연관관계 연결
        Users users = Users.createHost(signUpRequest.getBusinessNumber(), username, rawPassword, passwordEncoder);
        userRepository.save(users);

        Map<String, String> creds = new HashMap<>();
        creds.put("username", username);
        creds.put("password", rawPassword);
        // 저장
        return creds;
    }


    // host ID 생성
    private String nextHostUsername() {
        for (int width : new int[]{4,5,6}) {
            int max = (int) Math.pow(10, width) - 1;
            for (int i = 1; i <= max; i++) {
                String candidate = HOST_PREFIX + String.format("%0" + width + "d", i) + "!";
                if (!signInRepository.existsByUsername(candidate)) {
                    return candidate;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "생성 가능한 호스트 아이디가 없습니다");
    }
}

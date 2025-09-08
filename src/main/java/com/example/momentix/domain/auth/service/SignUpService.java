package com.example.momentix.domain.auth.service;

import com.example.momentix.domain.auth.dto.SignUpRequest;
import com.example.momentix.domain.auth.entity.RoleType;
import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.users.entity.Users;
import com.example.momentix.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final SignInRepository signInRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String HOST_PREFIX = "momentixHost";
    private static final DateTimeFormatter BIRTH_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public Long signUpUser(SignUpRequest dto) {
        // 400
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청(비밀번호 불일치)");
        }
        // 409
        if (signInRepository.existsByUsername(dto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다");
        }

        Users user = new Users();
        user.setNickname(dto.getNickname());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setBirthDate(parseBirth(dto.getBirthDate())); // 실패 시 400
        user.setRole(RoleType.CONSUMER);

        SignIn signIn = new SignIn();
        signIn.setUsername(dto.getUsername());
        signIn.setPassword(passwordEncoder.encode(dto.getPassword()));
        signIn.setUsers(user);
        user.setSignIn(signIn);

        try {
            userRepository.save(user);
            signInRepository.save(signIn);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 값입니다");
        }
        return user.getUserId();
    }

    //호스트 가입(입력은 사업자번호 하나, username/password는 자동 생성 & 동일)
    @Transactional
    public Map<String, String> signUpHost(SignUpRequest dto) {
        if (userRepository.existsByBusinessNumber(dto.getBusinessNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 사업자 번호입니다.");
        }

        String username = nextHostUsername(); // ex) momentixHost0001!
        String rawPassword = username;        // 아이디와 비밀번호 동일

        Users user = new Users();
        user.setBusinessNumber(dto.getBusinessNumber());
        user.setRole(RoleType.HOST);

        SignIn signIn = new SignIn();
        signIn.setUsername(username);
        signIn.setPassword(passwordEncoder.encode(rawPassword));
        signIn.setUsers(user);
        user.setSignIn(signIn);

        try {
            userRepository.save(user);
            signInRepository.save(signIn);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 값입니다");
        }

        return Map.of("username", username, "password", rawPassword);
    }


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

    private static LocalDate parseBirth(String yyyyMMdd) {
        try { return LocalDate.parse(yyyyMMdd, BIRTH_FMT); }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청(생년월일 형식: yyyyMMdd)");
        }
    }
}

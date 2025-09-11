package com.example.momentix.domain.users.service;

import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.users.entity.Users;
import com.example.momentix.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final SignInRepository signInRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void withdrawSelf(String username, String rawPassword) {
        // 1) 계정 조회 (isDeleted=false)
        SignIn signIn = signInRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정을 찾을 수 없습니다."));

        // 2) 비밀번호 확인
        if (!passwordEncoder.matches(rawPassword, signIn.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        // 3) Users 하드 삭제 + SignIn tombstone
        Users users = signIn.getUser();
        if (users == null) {
            throw new ResponseStatusException(HttpStatus.GONE, "이미 탈퇴한 계정입니다.");
        }

        // SignIn을 tombstone 상태로
        signIn.markAsWithdrawn();

        // Users는 PII 제거 목적의 하드 삭제
        userRepository.delete(users);
    }

}

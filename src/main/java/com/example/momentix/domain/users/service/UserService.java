package com.example.momentix.domain.users.service;

import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.users.dto.UserRequestDto;
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");
        }

        // 3) Users 하드 삭제 + SignIn tombstone(소프트딜리트)
        Users users = signIn.getUser();
        if (users == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 탈퇴한 사용자, 존재하지 않는 사용자");
        }

        // SignIn을 tombstone(소프트딜리트) 상태로
        signIn.markAsWithdrawn();

        // Users는 PII 제거 목적의 하드 삭제
        userRepository.delete(users);
    }

    // 닉넴/휴대폰/새 비번 한 번에 수정
    @Transactional
    public void updateUserInfo(String username, UserRequestDto req) {
        SignIn signIn = signInRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 탈퇴한 사용자, 존재하지 않는 사용자"));

        Users users = signIn.getUser();
        if (users == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 탈퇴한 사용자, 존재하지 않는 사용자");
        }

        boolean changed = false;

        // 닉네임
        if (req.getNickname() != null && !req.getNickname().isBlank()
                && !req.getNickname().equals(users.getNickname())) {
            users.setNickname(req.getNickname());
            changed = true;
        }


        userRepository.save(users);
        signInRepository.save(signIn);
    }

}

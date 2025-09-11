package com.example.momentix.domain.users.controller;

import com.example.momentix.domain.users.dto.UserRequestDto;
import com.example.momentix.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 본인 탈퇴: principal에서 username(이메일)만 있는 구조일 때
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawMe(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody UserRequestDto userDeleteRquestDto
    ) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 필요");
        }
        userService.withdrawSelf(principal.getUsername(), userDeleteRquestDto.getPassword());
    }

    //유저 개인정보 수정
    @PutMapping("/user-info")
    public void updateUserInfo(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody UserRequestDto userRquestDto
    ){
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 필요");
        }
        userService.updateUserInfo(principal.getUsername(), userRquestDto);
    }
}

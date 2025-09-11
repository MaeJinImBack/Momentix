package com.example.momentix.domain.users.controller;

import com.example.momentix.domain.users.dto.UserDeleteRquestDto;
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
            @RequestBody UserDeleteRquestDto userDeleteRquestDto
    ) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 필요");
        }
        userService.withdrawSelf(principal.getUsername(), userDeleteRquestDto.getPassword());
    }
}

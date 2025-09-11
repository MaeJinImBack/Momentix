package com.example.momentix.domain.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    private String password;

    private String nickname;
    private String phoneNumber;
    private String newPassword;
    private String newConfirmPassword;
}

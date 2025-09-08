package com.example.momentix.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    public interface UserSignUp {}
    public interface HostSignUp {}

    @NotBlank(groups = UserSignUp.class)
    private String username;

    @NotBlank(groups = UserSignUp.class)
    private String password;

    @NotBlank(groups = UserSignUp.class)
    private String confirmPassword;

    @NotBlank(groups = UserSignUp.class)
    private String nickname;

    @NotBlank(groups = UserSignUp.class)
    @Pattern(regexp = "\\d{8}", message = "생년월일은 8자리여야 합니다.")
    private String birthDate;

    @NotBlank(groups = UserSignUp.class)
    @Pattern(regexp = "\\d{10,11}", message = "휴대폰번호는 11자리 숫자여야 합니다.")
    private String phoneNumber;

    @NotBlank(groups = HostSignUp.class)
    @Pattern(regexp = "\\d{10}", message = "사업자번호는 10자리 숫자여야 합니다.")
    private String businessNumber;
}

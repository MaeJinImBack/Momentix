package com.example.momentix.domain.auth.service;

import com.example.momentix.domain.auth.dto.SignUpRequest;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SignUpServiceTest {
    // 비밀번호 불일치 테스트

    @Mock
    private UserRepository userRepository;
    @Mock
    private SignInRepository signInRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private SignUpService signUpService;

    //01. 비번 불일치 400
    @Test
    public void 회원가입비밀번호불일치에러발생(){
        //given
        SignUpRequest signUpRequest = new SignUpRequest(
                "hansol1212@test.com",
                "1233",
                "4566",
                "ninkanme",
                "20000219",
                "01029388457",
                null
        );
        // when & then
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> signUpService.signUpUser(signUpRequest));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("400 BAD_REQUEST \"잘못된 요청(비밀번호 불일치)\"", ex.getMessage());
    }
    //then

    // 02. 중복 아이디409
    
    // 03. 정상가입 userId 반환
}

package com.example.momentix.domain.auth.service;

import com.example.momentix.domain.auth.dto.SignUpRequest;
import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.users.entity.Users;
import com.example.momentix.domain.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

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
                "1233",
                "4566",
                "nickname",
                "20000219",
                "010-2938-8457",
                null
        );
        // 아이디 중복 이런 거 없어서 스텁 필요없음
        // when & then
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> signUpService.signUpUser("\"hansol1212@test.com\"",signUpRequest));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("400 BAD_REQUEST \"잘못된 요청(비밀번호 불일치)\"", ex.getMessage());
    }

    // 02. 중복 아이디409
    @Test
    public void 중복아이디(){

        // given
        SignUpRequest signUpRequest = new SignUpRequest(
                "1234",
                "1234",
                "nickname",
                "20000219",
                "010-2938-8457",
                null
        );
        given(signInRepository.existsByUsername("user1")).willReturn(true); //willReturn이란, 어떤 값을 돌려줘라~라고 가짜 객체의 동작을 지정할 때 쓰는 문법

        //when&then
        ResponseStatusException ex =assertThrows(ResponseStatusException.class,()-> signUpService.signUpUser("user1",signUpRequest));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("409 CONFLICT \"이미 사용 중인 아이디입니다\"", ex.getMessage());

    }
    
    // 03. 정상가입 userId 반환
    @Test
    public void 회원가입정상가입시userId반환(){
        SignUpRequest signUpRequest = new SignUpRequest(
                "1234",
                "1234",
                "nickname",
                "2000-02-19",
                "01029388457",
                null
        );
        given(signInRepository.existsByUsername("user1")).willReturn(false);
        given(passwordEncoder.encode("1234")).willReturn("1234");
        given(userRepository.save(any(Users.class))).willAnswer(i ->{
            Users u = i.getArgument(0);
            ReflectionTestUtils.setField(u, "userId", 1L);
            return u;
        });

        //when
        Long userId = signUpService.signUpUser("user1",signUpRequest);

        //then
        assertNotNull(userId);
        assertEquals(userId, 1L);
    }
}

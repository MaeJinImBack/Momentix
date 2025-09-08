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
        // 400: 비밀번호 확인 로직
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청(비밀번호 불일치)");
        }
        // 409: 아이디 중복 확인
        if (signInRepository.existsByUsername(dto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다");
        }

        // 임포트와 인스턴스화 차이점(왜 이렇게 엔티티를 만들어야 했냐)
        // 단순히 임포트하여 사용하게 되면, 사용 가능 상태로 준비만 하는 것.
        // 인스턴스화는 실제로 데이터를 담을 그릇을 만드는 것. (new+set으로 실제 인스턴스를 만들고 DTO 값들을 옮겨 담아야 DB에 저장할 수 있음)
        Users users = new Users(); // 새로운 User객체 하나 만듦
        users.setNickname(dto.getNickname()); //DTO에서 닉넴 꺼내서 Users객체에 담음
        users.setPhoneNumber(dto.getPhoneNumber());
        users.setBirthDate(LocalDate.parse(dto.getBirthDate())); // 실패 시 400
        users.setRole(RoleType.CONSUMER);

        SignIn signIn = new SignIn();
        signIn.setUsername(dto.getUsername());
        signIn.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 객체 <-> 테이블의 연관관계를 코드로 연결하는 작업
        signIn.setUsers(users);//“주민등록부에 이 집 주인이 누구인지 기록하기” (= DB FK 기록)
        users.setSignIn(signIn);//“집 안 냉장고에 내 주민등록증 복사본 붙여놓기” (= 코드 상에서 편하게 서로 참조)

        try {
            userRepository.save(users);// DB에 새로운 유저 정보를 저장함
            signInRepository.save(signIn);// 이어서 로그인 계정 정보를 저장함
        } catch (DataIntegrityViolationException e) { //DataIntegrityViolationException: 데이터 무결성 위반 + 유니크 제약 조건 위반 에러
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 값입니다");
        }
        return users.getUserId();
    }

    //호스트 가입(입력은 사업자번호 하나, username/password는 자동 생성 & 동일)
    @Transactional
    public Map<String, String> signUpHost(SignUpRequest dto) {
        if (userRepository.existsByBusinessNumber(dto.getBusinessNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 사업자 번호입니다.");
        }

        String username = nextHostUsername(); // ex) momentixHost0001!
        String rawPassword = username;        // 아이디와 비밀번호 동일

        Users users = new Users();
        users.setBusinessNumber(dto.getBusinessNumber());
        users.setRole(RoleType.HOST);

        SignIn signIn = new SignIn();
        signIn.setUsername(username);
        signIn.setPassword(passwordEncoder.encode(rawPassword));
        signIn.setUsers(users);
        users.setSignIn(signIn);

        try {
            userRepository.save(users);
            signInRepository.save(signIn);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 값입니다");
        }

        return Map.of("username", username, "password", rawPassword);
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

//    // 하이픈(-) 없애기
//    private static LocalDate parseBirth(String yyyyMMdd) {
//        try { return LocalDate.parse(yyyyMMdd, BIRTH_FMT); }
//        catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청(생년월일 형식: yyyyMMdd)");
//        }
//    }
}

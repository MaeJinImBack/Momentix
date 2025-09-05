package com.example.momentix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
public class MomentixApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomentixApplication.class, args);

        //회원가입 구현 이후에 지울 예정(임시 유저 비밀번호 암호화)
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1234";
        String encoded = encoder.encode(rawPassword);
        System.out.println("1234 암호화된 비번 = " + encoded);
    }

}

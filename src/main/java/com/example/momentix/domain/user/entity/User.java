package com.example.momentix.domain.user.entity;

import com.example.momentix.domain.auth.entity.SignIn;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="users")
@Getter
@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 20)  // 3~20자 닉네임
    private String nickname;

    @Column( length = 11)  // 01012345678
    private String phoneNumber;

    @Column( length = 10)
    private String birthDate;

    @Column(length = 15)  // host만 입력 (사업자번호)
    private String businessNumber;

    @Column(nullable = false, length = 20)
    private String role;   // "USER", "ADMIN", "HOST"

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SignIn signIn;

}

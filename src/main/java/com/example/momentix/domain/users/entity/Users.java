package com.example.momentix.domain.users.entity;

import com.example.momentix.domain.auth.entity.RoleType;
import com.example.momentix.domain.auth.entity.SignIn;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="users")
@Getter
@Entity
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 10)  // 3~20자 닉네임
    private String nickname;

    @Column( length = 11)  // 01012345678
    private String phoneNumber;

    @Column( length = 8)
    private String birthDate;

    @Column(length = 10)  // host만 입력 (사업자번호)
    private String businessNumber;

    @Enumerated(EnumType.STRING)
    private RoleType role;   // "USER", "ADMIN", "HOST"

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SignIn signIn;

}

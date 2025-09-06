package com.example.momentix.domain.users.entity;

import com.example.momentix.domain.auth.entity.RoleType;
import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.common.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name="users")
@Getter
@Entity
@NoArgsConstructor
public class Users extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 10)
    private String nickname;

    @Column( length = 11)  // 01012345678
    private String phoneNumber;

    @Column( length = 8)
    private LocalDate birthDate;

    @Column(length = 10)  // host만 입력 (사업자번호)
    private String businessNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType role;   // "USER", "ADMIN", "HOST"

    @Column(length = 12)
    private String slackId;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SignIn signIn;
}

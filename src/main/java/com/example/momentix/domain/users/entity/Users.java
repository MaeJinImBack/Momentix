package com.example.momentix.domain.users.entity;

import com.example.momentix.domain.auth.dto.SignUpRequest;
import com.example.momentix.domain.auth.entity.RoleType;
import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.common.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    public static Users createConsumer(SignUpRequest dto, PasswordEncoder encoder) {
        Users users = new Users();
        users.setRole(RoleType.CONSUMER);
        users.setNickname(dto.getNickname());
        users.setPhoneNumber(dto.getPhoneNumber());
        users.setBirthDate(LocalDate.parse(dto.getBirthDate()));

        SignIn signIn = SignIn.create(dto.getUsername(), dto.getPassword(), encoder, users);
        users.setSignIn(signIn);

        return users;
    }

    public static Users createHost(String businessNumber, String username, String rawPassword, PasswordEncoder encoder) {
        Users users = new Users();
        users.setRole(RoleType.HOST);
        users.setBusinessNumber(businessNumber);

        SignIn signIn = SignIn.create(username, rawPassword, encoder, users);
        users.setSignIn(signIn);

        return users;
    }

    public void setNickname(String nickname){ this.nickname = nickname; }
    public void setPhoneNumber(String phoneNumber){ this.phoneNumber = phoneNumber; }
    public void setBirthDate(LocalDate birthDate){ this.birthDate = birthDate; }
    public void setBusinessNumber(String businessNumber){ this.businessNumber = businessNumber; }
    public void setRole(RoleType role){ this.role = role; }
    public void setSignIn(SignIn signIn){ this.signIn = signIn; }
}

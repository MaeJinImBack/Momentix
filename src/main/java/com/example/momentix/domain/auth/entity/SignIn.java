package com.example.momentix.domain.auth.entity;

import com.example.momentix.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Table(name="Signin")
@Entity
@NoArgsConstructor
public class SignIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long signInId;

    @Column(nullable = false, unique = true)
    private String username; // 로그인 ID(컨슈머는 이메일)

    @Column(nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false, unique = true)
    private Users users;

    //캡슐화, new+set을 안해도 되게 만듦
    public static SignIn create(String username, String rawPassword, PasswordEncoder encoder, Users users) {
        SignIn signIn = new SignIn();
        signIn.setUsername(username);
        signIn.setPassword(encoder.encode(rawPassword));
        //Users에도 setSignIn()을 해줘야 양방향 관계 완성됨
        signIn.setUsers(users);
        return signIn;
    }

    public String getUsername() {return username;}

    public String getPassword() {
        return password;
    }
    public Users getUser() {
        return users;
    }


    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setUsers(Users users) { this.users = users; }
}

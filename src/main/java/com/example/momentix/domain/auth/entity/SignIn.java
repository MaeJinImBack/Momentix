package com.example.momentix.domain.auth.entity;

import com.example.momentix.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Table(name="signin")
@Entity
@NoArgsConstructor
public class SignIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long signInId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false, unique = true)
    private Users users;

    public String getUsername() {return username;}

    public String getPassword() {
        return password;
    }
    public Users getUser() {
        return users;
    }
}

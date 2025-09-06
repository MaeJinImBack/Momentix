package com.example.momentix.domain.auth.entity;

import com.example.momentix.domain.users.entity.User;
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
    private User user;

    public String getUsername() {return username;}

    public String getPassword() {
        return password;
    }
    public User getUser() {
        return user;
    }
}

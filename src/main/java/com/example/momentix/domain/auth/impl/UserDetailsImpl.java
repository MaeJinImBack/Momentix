package com.example.momentix.domain.auth.impl;

import com.example.momentix.domain.auth.entity.RoleType;
import com.example.momentix.domain.auth.entity.SignIn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final SignIn signIn;

    // 회원가입 때 role이 저장됨 <- hasRole에서 가져와서.
    // 로그인하면 DB에서 role을 꺼내옴 getAuthorities()
    //  "ROLE"+role -> "ROLE_USER"형태로 감쌈
    // 이 값이 시큐리티 컨텍스트에 올라감
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        RoleType role = signIn.getUser().getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" +  role.name()));
    }

    // 나중에 서비스/컨트롤러에서 바로 쓰게
    public Long getUserId(){
        return signIn.getUser().getUserId();
    }

    public RoleType getRole() {
        return signIn.getUser().getRole();
    }

    public String getRoleName() {
        return signIn.getUser().getRole().name();
    }

    @Override
    public String getPassword() {
        return signIn.getPassword();
    }

    @Override
    public String getUsername() {
        return signIn.getUsername();
    }


    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

package com.example.momentix.domain.auth.impl;

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

    //DB에 role 없어서 코드에서 직접 부여하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = signIn.getUser().getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
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

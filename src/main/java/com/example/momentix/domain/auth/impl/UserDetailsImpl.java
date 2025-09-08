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

    // 현재 로그인한 사용자의 PK(userId)를 반환
    //본인 소유 리소스(리뷰, 예매, 공연 등)인지 확인할 때 사용

    public Long getUserId(){
        return signIn.getUser().getUserId();
    }

    //현재 로그인한 사용자의 RoleType(ADMIN, HOST, CONSUMER)을 반환
    //권한별 분기 처리(Service/Controller 로직)에서 사용
    public RoleType getRole() {
        return signIn.getUser().getRole();
    }

    //현재 로그인한 사용자의 권한 이름을 문자열로 반환
    //주로 로깅, 디버깅, 응답 DTO 등 문자열 권한이 필요한 곳에서 사용
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

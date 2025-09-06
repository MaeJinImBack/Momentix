package com.example.momentix.domain.auth.impl;

import com.example.momentix.domain.auth.entity.SignIn;
import com.example.momentix.domain.auth.repository.SignInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

private final SignInRepository signInRepository;

    //로그인할 때 아이디(username)로 DB에서 사용자 찾아서, Spring Security가 이해할 수 있는 형태(UserDetailsImpl)로 바꿔주는 역할
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SignIn signIn = signInRepository.findWithUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found:"+username));
        return new UserDetailsImpl(signIn);//권한 부여
    }
}

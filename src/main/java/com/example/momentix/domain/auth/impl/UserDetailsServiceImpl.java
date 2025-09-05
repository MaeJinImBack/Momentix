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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SignIn signIn = signInRepository.findWithUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found:"+username));
        return new UserDetailsImpl(signIn);//권한 부여
    }
}

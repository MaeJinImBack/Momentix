package com.example.momentix.domain.auth.service;

import com.example.momentix.domain.auth.repository.SignInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService {
    private final SignInRepository signInRepository;
    private final PasswordEncoder passwordEncoder;

}

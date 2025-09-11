package com.example.momentix.domain.users;

import com.example.momentix.domain.auth.repository.SignInRepository;
import com.example.momentix.domain.users.repository.UserRepository;
import com.example.momentix.domain.users.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private SignInRepository signInRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


}

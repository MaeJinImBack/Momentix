package com.example.momentix.config;


import com.example.momentix.domain.common.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/sign-in", "/auth/refresh").permitAll() //오른쪽은 리프레시
                                // 엔드포인트 위해 넣어줌
                        .requestMatchers("/auth/me").authenticated() // 나중에 지울 예정
                        .anyRequest().permitAll()
                );
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 인증 핵심 빈
    //AuthenticationManager: 사용자의 아이디/비밀번호가 맞는지 검사하는 주체.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //DB기반 로그인 처리
    //AuthenticationProvider: 매니저가 직접 인증을 수행하지 않고 여러 프로바이더에 위임해서 인증 처리함
    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // 1) 로그인 시 아이디(username)로 DB에서 사용자 정보 조회
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        // 2) 조회된 사용자 비밀번호와 로그인 시도한 비밀번호를 비교 (암호화된 값 기준)
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        // 3) 최종적으로 AuthenticationManager에 등록될 Provider 반환
        return daoAuthenticationProvider;
    }
}

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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity //메서드 보안 켜기
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthorizationFilter jwtAuthorizationFilter,
                                           AuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/redis/test").permitAll()
                        // host 공연등록 가능
                        .requestMatchers(HttpMethod.POST, "/events").hasRole("HOST")
                        .requestMatchers(HttpMethod.POST, "/seats/**").hasRole("HOST")

                        //admin
                            // 이부분(리뷰 삭제 가능), 나중에 컨트롤러/서비스에서 본인 여부 검사 코드 넣어주셔야 합니다.
                            //amdin은 모든 리뷰 삭제가 가능한 점,
                            // consumer는 본인 리뷰만 삭제 가능한 점 고려해 주세요
                            // if (me.getRole() == RoleType.ADMIN) 대충 요런 느낌
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasAnyRole("CONSUMER","ADMIN")
                        // ADMIN 공연삭제
                        .requestMatchers(HttpMethod.DELETE, "/events/**").hasRole("ADMIN")

                        //블랙리스트 차단 ADMIN API ADD

                        // 알림
                        .requestMatchers(HttpMethod.POST, "/notifications/broadcast").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/notifications/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/notifications/favorites/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/notifications/events/**").hasAnyRole("ADMIN","HOST")

                        .requestMatchers("/error").permitAll()
                        
                        // 특정 조건을 지정하지 않은 나머지 모든 API는 로그인만 했으면 호출 가능함
                        .anyRequest().authenticated() //문제점: host/admin도 가능해짐
//                        .requestMatchers(HttpMethod.POST, "/reservations/**").hasRole("CONSUMER")
//                        .requestMatchers(HttpMethod.POST, "/payments/**/cancel").hasRole("CONSUMER")
                )
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // SignInService 에서 주입받아 쓰고 있으므로 제공 필요
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //DB기반 로그인 처리
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder
            ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}

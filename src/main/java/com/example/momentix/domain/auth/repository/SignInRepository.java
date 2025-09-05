package com.example.momentix.domain.auth.repository;

import com.example.momentix.domain.auth.entity.SignIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SignInRepository extends JpaRepository<SignIn,Long> {
   // 로그인아이디, 권한, PK가 토큰에 들어가기 때문에.
    @Query("""
        select s
        from SignIn s
        join fetch s.user u
        where s.username = :username
    """)
    Optional<SignIn> findWithUserByUsername(@Param("username") String username);

    //username으로 SignIn만 조회: 단순히 로그인 요청 시 DB에서 계정(SignIn) 존재 여부 확인할 때 사용
    Optional<SignIn> findByUsername(String username);

    // refresh 토큰에서 꺼낸 userId로 조회
    Optional<SignIn> findByUser_UserId(Long userId);
}

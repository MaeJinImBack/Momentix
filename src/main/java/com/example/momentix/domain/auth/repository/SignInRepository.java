package com.example.momentix.domain.auth.repository;

import com.example.momentix.domain.auth.entity.SignIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SignInRepository extends JpaRepository<SignIn,Long> {
    @Query("""
        select s
        from SignIn s
        join fetch s.user u
        where s.username = :username
    """)
    Optional<SignIn> findWithUserByUsername(@Param("username") String username);

    Optional<SignIn> findByUsername(String username);

    // refresh 토큰에서 꺼낸 userId로 조회
    Optional<SignIn> findByUser_UserId(Long userId);
}

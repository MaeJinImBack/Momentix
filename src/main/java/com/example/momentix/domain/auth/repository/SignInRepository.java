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
        join fetch s.users u
        where s.username = :username
    """)
    Optional<SignIn> findWithUserByUsername(@Param("username") String username);

 @Query("select u.userId from SignIn s join s.users u where s.username = :username")
 Optional<Long> findUserIdByUsername(@Param("username") String username);

}

package com.example.momentix.domain.auth.repository;

import com.example.momentix.domain.auth.entity.SignIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SignInRepository extends JpaRepository<SignIn,Long> {
   // SignIn과 Users를 한 번에 불러오기
    @Query("""
        select s
        from SignIn s
        join fetch s.users u
        where s.username = :username
    """)
    Optional<SignIn> findWithUserByUsername(@Param("username") String username);
    
    // username으로 Users.userId만 추출 (Users가 이미 삭제돼 null일 수도 있음)
 @Query("select u.userId from SignIn s join s.users u where s.username = :username")
 Optional<Long> findUserIdByUsername(@Param("username") String username);

 // 회원가입 중복 검사(host 계정 가입 시 사업자번호)
    boolean existsByUsername(String username);

    @Query("""
    select s
    from SignIn s
    join fetch s.users u
    where u.businessNumber = :businessNumber
""")
    Optional<SignIn> findWithUserByBusinessNumber(@Param("businessNumber") String businessNumber);

    Optional<SignIn> findByUsername(String email);

    //로그인/비밀번호 확인용
    Optional<SignIn> findByUsernameAndIsDeletedFalse(String username);
    
    //추후에 효율적으로 정리예정
}

package com.example.momentix.domain.point.repository;

import com.example.momentix.domain.point.entity.Points;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointsRepository extends JpaRepository <Points, Long>{
    
    Optional<Points> findByUserId(Long userId);
    
    //비관적 락: 해당 유저 행만 잠금
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select points from Points points where points.userId=:userId")
    Optional<Points> findForUpdate(@Param("userId") Long userId);

    // 트래픽 증가 시 전환
    @Modifying
    @Query(value = """
        UPDATE points
           SET point_balance = point_balance + :delta
         WHERE user_id = :userId
           AND (:delta >= 0 OR point_balance >= :absDelta)
        """, nativeQuery = true)
    int updateBalanceIfEnough(@Param("userId") Long userId,
                              @Param("delta") long delta,
                              @Param("absDelta") long absDelta);
}

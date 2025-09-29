package com.example.momentix.domain.point.repository;

import com.example.momentix.domain.point.entity.Points;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointsRepository extends JpaRepository<Points, Long> {

    Optional<Points> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select points from Points points where points.userId=:userId")
    Optional<Points> findForUpdate(@Param("userId") Long userId);
}

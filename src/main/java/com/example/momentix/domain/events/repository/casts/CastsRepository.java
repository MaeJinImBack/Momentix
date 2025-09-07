package com.example.momentix.domain.events.repository.casts;

import com.example.momentix.domain.events.entity.casts.Casts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CastsRepository extends JpaRepository<Casts, Long> {
    // 출연자 이름으로 검색
    Optional<Casts> findByCastName(String castName);
}

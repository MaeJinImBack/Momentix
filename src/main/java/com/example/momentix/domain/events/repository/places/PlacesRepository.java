package com.example.momentix.domain.events.repository.places;

import com.example.momentix.domain.events.entity.places.Places;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlacesRepository extends JpaRepository<Places, Long> {
    // 공연장 이름으로 찾기
    Optional<Places> findByPlaceName(String placeName);
}

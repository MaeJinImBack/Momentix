package com.example.momentix.domain.events.repository.reservationtimes;

import com.example.momentix.domain.events.entity.reservationtimes.ReservationTimes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationTimesRepository extends JpaRepository<ReservationTimes, Long> {

}

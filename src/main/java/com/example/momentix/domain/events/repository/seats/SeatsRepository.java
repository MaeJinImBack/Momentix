package com.example.momentix.domain.events.repository.seats;

import com.example.momentix.domain.events.entity.seats.Seats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatsRepository extends JpaRepository<Seats, Long> {
}

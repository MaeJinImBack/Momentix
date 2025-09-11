package com.example.momentix.domain.events.repository.eventtimes;

import com.example.momentix.domain.events.entity.eventtimes.EventTimeReserveSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventTimeReserveSeatRepository
        extends JpaRepository<EventTimeReserveSeat, Long>, EventTimeReserveSeatRepositoryCustom {
    Optional<Object> findByEventTimes_Id(Long eventTimeId);
}

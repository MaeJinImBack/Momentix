package com.example.momentix.domain.events.repository.eventtimes;

import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTimesRepository extends JpaRepository<EventTimes, Long> {


    boolean existsByIdAndEventsId(Long id, Long eventsId);

    Optional<EventTimes> findByIdAndEventsId(Long eventTimeId, Long eventsId);
}

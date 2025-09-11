package com.example.momentix.domain.events.repository;

import com.example.momentix.domain.events.entity.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {
    @Modifying
    @Query("DELETE " +
            "FROM EventSeat es " +
            "where es.events.id = :eventsId")
    void deleteByEventsId(@Param("eventsId") Long eventsId);
}

package com.example.momentix.domain.ticket.repository;

import com.example.momentix.domain.ticket.entity.Tickets;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Tickets, Long> {

    @EntityGraph(attributePaths = {
            "eventTime", "eventTime.events", "seat", "seat.places", "paymentHistory"
    })
    List<Tickets> findByUsers_UserIdOrderByTicketIdDesc(Long userId);

    @EntityGraph(attributePaths = {
            "eventTime", "eventTime.events", "seat", "seat.places", "paymentHistory"
    })
    Optional<Tickets> findByTicketIdAndUsers_UserId(Long ticketId, Long userId);
}

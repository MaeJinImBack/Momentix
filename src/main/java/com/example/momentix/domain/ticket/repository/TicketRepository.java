package com.example.momentix.domain.ticket.repository;

import com.example.momentix.domain.ticket.entity.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Tickets, Long> {
}
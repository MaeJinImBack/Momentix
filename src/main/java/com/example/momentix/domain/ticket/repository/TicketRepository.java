package com.example.momentix.domain.ticket.repository;

import com.example.momentix.domain.ticket.entity.Tickets;
import com.example.momentix.domain.users.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Page<Tickets> findByUsersAndIsDeletedFalse(Users user, Pageable pageable);

    // 외래키 주인이 티켓이라 결정확정 때 티켓 행에 결제 ID를 넣어야 함
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update Tickets t
              set t.paymentHistory.paymentHistoryId = :paymentHistoryId
            where t.ticketId = :ticketId
           """)
    int linkPayment(@Param("ticketId") Long ticketId,
                    @Param("paymentHistoryId") Long paymentHistoryId);
}

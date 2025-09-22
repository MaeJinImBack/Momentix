package com.example.momentix.domain.paymenthistory.repository;

import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import com.example.momentix.domain.paymenthistory.entity.PaymentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    boolean existsByReservationIdAndPaymentStatusType(Long reservationId, PaymentStatusType status);
    default boolean existsPendingByReservation(Long reservationId) {
        return existsByReservationIdAndPaymentStatusType(reservationId, PaymentStatusType.PENDING);
    }
}
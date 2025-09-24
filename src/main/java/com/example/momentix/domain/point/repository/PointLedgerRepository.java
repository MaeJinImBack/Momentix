package com.example.momentix.domain.point.repository;

import com.example.momentix.domain.point.entity.PointLedger;
import com.example.momentix.domain.point.entity.PointOperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointLedgerRepository extends JpaRepository<PointLedger, Long> {
    boolean existsByUserIdAndIdempotencyKey(Long userId, String idempotencyKey);

    //결제 단위 사용 합계(환급 시)
    @Query(
            "select coalesce(sum(l.amount),0) from PointLedger l " +
                    "where l.userId = :userId and l.pointOperationType = :op and l.relatedPaymentId = :paymentId")
    long sumAmountByPaymentAndOp(@Param("userId") Long userId,
                                 @Param("paymentId") Long paymentId,
                                 @Param("op") PointOperationType op);

    // 결제 단위 적립 예정 합계(릴리즈/취소 시 사용)
    @Query(
            "select coalesce(sum(l.amount),0) from PointLedger l " +
                    "where l.userId = :userId and l.pointOperationType = com.example.momentix.domain.point.entity" +
                    ".PointOperationType.PENDING_EARN " +
                    "and l.relatedPaymentId = :paymentId")
    long sumPendingEarnByPayment(@Param("userId") Long userId, @Param("paymentId") Long paymentId);

    boolean existsByUserIdAndPointOperationTypeAndRelatedPaymentId(Long userId,
                                                                   PointOperationType pointOperationType,
                                                                   Long relatedPaymentId);
}
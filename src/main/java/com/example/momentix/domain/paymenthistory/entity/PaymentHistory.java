package com.example.momentix.domain.paymenthistory.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Table(name = "payment_history")
@Entity
@Getter
public class PaymentHistory extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentHistoryId;

    // 예약 ID, 티켓발급 시 필요한 키
    @Column(nullable = false)
    private Long reservationId;

    //결제자
    @Column(name = "payer")
    private String payer;

    //결제 수단
    @Column(name = "payment_method")
    private String paymentMethod;

    //결제금액
    @Column(name = "payment_price")
    private BigDecimal paymentPrice;

    //결제 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatusType paymentStatusType;

    // 매개변수 없는 생성자 = No-Args-Constructor
   protected PaymentHistory(){
    }

    public static PaymentHistory create(Long reservationId, String payer, String method, BigDecimal price){
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.reservationId = reservationId;
        paymentHistory.payer = payer;
        paymentHistory.paymentMethod = method;
        paymentHistory.paymentPrice = price == null ? BigDecimal.ZERO : price;
        paymentHistory.paymentStatusType = PaymentStatusType.PENDING;
        return paymentHistory;
    }


    public void markSuccess() { this.paymentStatusType = PaymentStatusType.SUCCESS; }
    public void markFailed()  { this.paymentStatusType = PaymentStatusType.FAILED; }
    public void markCancel()  { this.paymentStatusType = PaymentStatusType.CANCEL; }

    public Long getPaymentHistoryId() { return paymentHistoryId; }
    public Long getReservationId() { return reservationId; }
    public String getPayer() { return payer; }
    public String getPaymentMethod() { return paymentMethod; }
    public BigDecimal getPaymentPrice() { return paymentPrice; }
    public PaymentStatusType getPaymentStatusType() { return paymentStatusType; }
}
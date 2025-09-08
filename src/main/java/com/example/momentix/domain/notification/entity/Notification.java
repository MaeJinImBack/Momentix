package com.example.momentix.domain.notification.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(name = "message")
    private String message;

    @Column(name = "delivery_time")
    @DateTimeFormat
    private LocalDateTime deliveryTime;

    @Column(name = "reservation_status_type")
    @Enumerated(EnumType.STRING)
    private ReservationStatusType reservationStatusType;



}

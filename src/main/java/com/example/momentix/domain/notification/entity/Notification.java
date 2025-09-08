package com.example.momentix.domain.notification.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(name = "messsage")
    private String message;

    @Column(name = "deliveryTime")
    @DateTimeFormat
    private LocalDateTime deliveryTime;

    @Column(name = "reservationStatusType")
    @Enumerated(EnumType.STRING)
    private ReservationStatusType reservationStatusType;



}

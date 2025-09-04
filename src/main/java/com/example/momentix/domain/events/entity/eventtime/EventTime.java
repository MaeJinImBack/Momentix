package com.example.momentix.domain.events.entity.eventtime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EventTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long eventTimeId;
}

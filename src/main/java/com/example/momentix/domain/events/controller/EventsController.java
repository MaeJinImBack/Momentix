package com.example.momentix.domain.events.controller;

import com.example.momentix.domain.events.dto.request.CreateEventsRequestDto;
import com.example.momentix.domain.events.dto.response.EventsResponseDto;
import com.example.momentix.domain.events.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsController {

    private final EventsService eventsService;

    // 공연 등록 (생성)
    @PostMapping()
    public ResponseEntity<EventsResponseDto> createEvent(@RequestBody CreateEventsRequestDto requestDto) {
        EventsResponseDto response = eventsService.createEvent(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}

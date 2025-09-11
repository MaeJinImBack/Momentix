package com.example.momentix.domain.events.controller;

import com.example.momentix.domain.events.dto.request.CreateEventsRequestDto;
import com.example.momentix.domain.events.dto.request.UpdateBaseEventRequestDto;
import com.example.momentix.domain.events.dto.response.AllReadEventsResponseDto;
import com.example.momentix.domain.events.dto.response.EventsResponseDto;
import com.example.momentix.domain.events.dto.response.ReadEventResponseDto;
import com.example.momentix.domain.events.service.EventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping()
    public ResponseEntity<Page<AllReadEventsResponseDto>> readAllEvents(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {
        Page<AllReadEventsResponseDto> response = eventsService.allReadEvents(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Void> updateBaseEvent(@PathVariable Long eventId, @RequestBody UpdateBaseEventRequestDto requestDto) {
        eventsService.updateEvent(eventId, requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/{eventId}/{placeId}")
    public ResponseEntity<ReadEventResponseDto> readEvent(@PathVariable Long eventId, @PathVariable Long placeId) {
        ReadEventResponseDto response = eventsService.readEvent(eventId, placeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

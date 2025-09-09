package com.example.momentix.domain.events.controller;

import com.example.momentix.domain.events.dto.request.PlacesRequestDto;
import com.example.momentix.domain.events.dto.request.SeatRequestDto;
import com.example.momentix.domain.events.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class SeatController {
    private final SeatService seatService;

    @PostMapping("/{eventId}/seats")
    public ResponseEntity<List<SeatRequestDto>> createSeat(
            @RequestPart("file") MultipartFile seatFile,
            @RequestPart("request") PlacesRequestDto placeRequest,
            @PathVariable Long eventId) {
        return new ResponseEntity<>(seatService.createSeat(seatFile, placeRequest, eventId), HttpStatus.CREATED);
    }
}

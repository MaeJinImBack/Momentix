package com.example.momentix.domain.events.controller;

import com.example.momentix.domain.events.dto.request.PlacesRequestDto;
import com.example.momentix.domain.events.dto.response.PartRowColSeatResponseDto;
import com.example.momentix.domain.events.dto.response.ReserveSeatResponseDto;
import com.example.momentix.domain.events.dto.response.SeatResponseDto;
import com.example.momentix.domain.events.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class SeatController {
    private final SeatService seatService;

    @PostMapping("/{eventId}/seats")
    public ResponseEntity<List<SeatResponseDto>> createSeat(
            @RequestPart("file") MultipartFile seatFile,
            @RequestPart("request") PlacesRequestDto placeRequest,
            @PathVariable Long eventId) {
        return new ResponseEntity<>(seatService.createSeat(seatFile, placeRequest, eventId), HttpStatus.CREATED);
    }

    // 크기가 클 경우를 대비해서 Page, Id값으로 받는 이유 : id가 인덱스 효율이 더 좋음
    @GetMapping("/{eventId}/{placeId}/event-time/{eventTimeId}/seats")
    public ResponseEntity<Page<ReserveSeatResponseDto>> readAllSeats(
            @PathVariable Long eventId,
            @PathVariable Long placeId,
            @PathVariable Long eventTimeId,
            @PageableDefault Pageable pageable) {
        return new ResponseEntity<>(seatService.readSeatsAll(eventId, placeId, eventTimeId, pageable), HttpStatus.OK);
    }


    @GetMapping("/{eventId}/{placeId}/event-time/{eventTimeId}/seats")
    public ResponseEntity<Page<PartRowColSeatResponseDto>> readPartSeats(
            @PathVariable Long eventId,
            @PathVariable Long placeId,
            @PathVariable Long eventTimeId,
            @RequestParam(required = false) Long partId,
            @RequestParam(required = false) Long rowId,
            @RequestParam(required = false) Long colId,
            @PageableDefault Pageable pageable) {

        return new ResponseEntity<>(seatService.readSeatsPart(
                eventId, placeId, eventTimeId, partId, rowId, colId, pageable), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN','HOST')")
    @DeleteMapping("/{placeId}/seats")
    public ResponseEntity<Void> softDeleteSeats(
            @RequestPart("file") MultipartFile deleteFile,
            @PathVariable Long placeId) {
        seatService.deleteSeat(deleteFile, placeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

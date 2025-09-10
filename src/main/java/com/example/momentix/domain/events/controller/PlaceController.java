package com.example.momentix.domain.events.controller;

import com.example.momentix.domain.events.dto.request.PlacesRequestDto;
import com.example.momentix.domain.events.dto.response.BaseSeatResponseDto;
import com.example.momentix.domain.events.dto.response.PlaceResponseDto;
import com.example.momentix.domain.events.service.PlaceService;
import com.example.momentix.domain.events.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlaceController {

    public final PlaceService placeService;
    public final SeatService seatService;

    @PostMapping
    public ResponseEntity<PlaceResponseDto> createPlace(@RequestBody PlacesRequestDto placesRequest) {
        return new ResponseEntity<>(placeService.createPlace(placesRequest), HttpStatus.CREATED);
    }

    @PostMapping("/base-seat")
    public ResponseEntity<List<BaseSeatResponseDto>> createBaseSeat(
            @RequestPart("file") MultipartFile baseSeatFile,
            @RequestPart("request") PlacesRequestDto placesRequest) {
        List<BaseSeatResponseDto> response = seatService.createBaseSeat(baseSeatFile, placesRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}

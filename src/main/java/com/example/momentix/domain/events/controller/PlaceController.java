package com.example.momentix.domain.events.controller;

import com.example.momentix.domain.events.dto.request.PlacesRequestDto;
import com.example.momentix.domain.events.dto.response.PlaceResponseDto;
import com.example.momentix.domain.events.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlaceController {

    public final PlaceService placeService;

    @PostMapping
    public ResponseEntity<PlaceResponseDto> createPlace(@RequestBody PlacesRequestDto placesRequest) {
        return new ResponseEntity<>(placeService.createPlace(placesRequest), HttpStatus.CREATED);
    }

}

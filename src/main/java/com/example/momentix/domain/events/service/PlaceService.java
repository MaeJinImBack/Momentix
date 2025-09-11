package com.example.momentix.domain.events.service;

import com.example.momentix.domain.events.dto.request.PlacesRequestDto;
import com.example.momentix.domain.events.dto.response.PlaceResponseDto;
import com.example.momentix.domain.events.entity.places.Places;
import com.example.momentix.domain.events.repository.places.PlacesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlacesRepository placesRepository;

    @Transactional
    public PlaceResponseDto createPlace(PlacesRequestDto placesRequest) {
        Places place = placesRepository.findByPlaceName(placesRequest.getPlaceName()).orElseGet(() -> Places.builder()
                .placeName(placesRequest.getPlaceName())
                .placeAddress(placesRequest.getPlaceAddress())
                .build());
        placesRepository.save(place);

        return new PlaceResponseDto(place);
    }

}

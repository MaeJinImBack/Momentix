package com.example.momentix.domain.events.dto.response;

import com.example.momentix.domain.events.entity.places.Places;
import lombok.Getter;

@Getter
public class PlaceResponseDto {
    private String placeName;
    private String placeAddress;

    public PlaceResponseDto(Places place) {
        this.placeName = place.getPlaceName();
        this.placeAddress = place.getPlaceAddress();
    }
}

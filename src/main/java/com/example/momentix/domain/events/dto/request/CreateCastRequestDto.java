package com.example.momentix.domain.events.dto.request;

import lombok.Getter;

@Getter

public class CreateCastRequestDto {
    private String castName;
    private String castImageUrl;

    public CreateCastRequestDto(String castName, String castImageUrl) {
        this.castName = castName;
        this.castImageUrl = castImageUrl;
    }
}

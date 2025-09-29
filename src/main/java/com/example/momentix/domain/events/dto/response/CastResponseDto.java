package com.example.momentix.domain.events.dto.response;

import com.example.momentix.domain.events.entity.casts.Casts;
import lombok.Getter;

@Getter
public class CastResponseDto {
    private String castName;
    private String castImageUrl;

    public CastResponseDto(Casts casts) {
        this.castName = casts.getCastName();
        this.castImageUrl = casts.getCastImageUrl();
    }
}

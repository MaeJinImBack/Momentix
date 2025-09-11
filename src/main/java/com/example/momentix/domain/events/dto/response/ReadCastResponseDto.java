package com.example.momentix.domain.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadCastResponseDto {
    private String castName;
    private String castImageUrl;
}

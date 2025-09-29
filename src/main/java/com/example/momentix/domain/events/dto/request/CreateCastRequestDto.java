package com.example.momentix.domain.events.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateCastRequestDto {
    private String castName;
    private String castImage;
}

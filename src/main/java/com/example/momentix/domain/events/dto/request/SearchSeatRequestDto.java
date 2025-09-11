package com.example.momentix.domain.events.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchSeatRequestDto {
    private Long eventId;
    private Long placeId;
    private Long eventTimeId;

}

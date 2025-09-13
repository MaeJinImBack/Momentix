package com.example.momentix.domain.search.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class SearchResponseDto {
    private String eventTitle;
    private String placeName;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;

}

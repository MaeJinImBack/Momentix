package com.example.momentix.domain.search.repository;

import com.example.momentix.domain.search.dto.AutocompleteResponse;

import java.util.List;


//인기순 자동완성, completion suggester 호출
public interface SuggestRepository{

    List<AutocompleteResponse> suggest(String query, int limit) ;
}
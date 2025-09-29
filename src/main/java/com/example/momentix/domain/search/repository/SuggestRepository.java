package com.example.momentix.domain.search.repository;

import com.example.momentix.domain.search.dto.AutocompleteResponse;

import java.util.List;


//자동 완성 기능
public interface SuggestRepository{

    // 입력 글자(query) 기준으로 자동와성 후보 최대 limit개
    List<AutocompleteResponse> suggest(String query, int limit) ;
}
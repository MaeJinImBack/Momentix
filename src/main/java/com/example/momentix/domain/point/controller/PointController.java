package com.example.momentix.domain.point.controller;


import com.example.momentix.domain.point.service.PointService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService){
        this.pointService=pointService;
    }

    // 내 포인트 조회


    // 즉시 적립

    // 사용(차감)


    // 적립 에정
    
    //예정 해제
}

package com.example.momentix.domain.point.controller;


import com.example.momentix.domain.point.service.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService){
        this.pointService=pointService;
    }

    // 내 포인트 조회
    @GetMapping("/me")
    public ResponseEntity<> me(

    ){

    }

    // 즉시 적립
    @GetMapping("/earn")
    public ResponseEntity<> earn(

    ){

    }
    // 사용(차감)
    @GetMapping("/use")
    public ResponseEntity<> use(

    ){

    }

    // 적립 에정
    @GetMapping("/pending/earn")
    public ResponseEntity<> earnPending(

    ){

    }
    //예정 해제
    @GetMapping("/pending/release")
    public ResponseEntity<> releasePending(

    ){

    }
}

package com.example.backend.controller;

import com.example.backend.dto.GoogleDirectionsResponse;
import com.example.backend.dto.RouteRequestDTO;
import com.example.backend.dto.RouteResponseDTO;
import com.example.backend.dto.SearchRequest;
import com.example.backend.service.GoogleMapsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@CrossOrigin(origins = "http://localhost:5173")
public class RouteController {

    private final GoogleMapsService googleMapsService;

    // 의존성 주입
    public RouteController(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }

    // 리액트에서 axios.post로 던진 데이터를 받는 곳
    @PostMapping("/calculate")
    public GoogleDirectionsResponse calculateRoute(@RequestBody SearchRequest requset) {
        // 1. 서비스 호출해 구글 API 때리고 DTO로 받아오기
        return googleMapsService.getDirections(requset.getOrigin(), requset.getDestination());
    }
}

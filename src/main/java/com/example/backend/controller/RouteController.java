package com.example.backend.controller;

import com.example.backend.dto.RouteCalculateResponse;
import com.example.backend.dto.SearchRequest;
import com.example.backend.service.RouteCalculatorService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/routes")
@CrossOrigin(origins = "http://localhost:5173")
public class RouteController {

    private final RouteCalculatorService routeCalculatorService;

    public RouteController(RouteCalculatorService routeCalculatorService) {
        this.routeCalculatorService = routeCalculatorService;
    }

    @PostMapping("/calculate")
    public RouteCalculateResponse calculateResponse(@RequestBody SearchRequest request) {
        return routeCalculatorService.calculateRoute(request);
    }
}

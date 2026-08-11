package com.example.backend.controller;

import com.example.backend.dto.RouteCalculateResponse;
import com.example.backend.dto.RouteCalculateResponse.RouteOption;
import com.example.backend.dto.SearchRequest;
import com.example.backend.service.DirectRouteBuilder;
import com.example.backend.service.TransferRouteBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@CrossOrigin(origins = "http://localhost:5173")
public class RouteController {

    private final DirectRouteBuilder directRouteBuilder;
    private final TransferRouteBuilder multiTransferRouteBuilder;

    public RouteController(DirectRouteBuilder directRouteBuilder, TransferRouteBuilder multiTransferRouteBuilder) {
        this.directRouteBuilder = directRouteBuilder;
        this.multiTransferRouteBuilder = multiTransferRouteBuilder;
    }

    public RouteCalculateResponse calculateRoute(SearchRequest request) {
        String origin = request.getOrigin();
        String dest = request.getDestination();
        boolean isIC = "IC".equalsIgnoreCase(request.getPaymentType());

        boolean hasPass = request.getPassId() != null && !request.getPassId().equalsIgnoreCase("none");

        // 직통
        List<RouteOption> routes = new ArrayList<>(directRouteBuilder.buildDirectRoutes(origin, dest, isIC, hasPass));

        // 환승
        if (routes.size() < 3) {
            routes.addAll(multiTransferRouteBuilder.buildTransferRoutes(origin, dest, isIC, hasPass, 3 ,routes.size() + 1));
        }

        String note = "지하철 패스 보유 시 최저가 경로 이용 시 무료입니다";

        return RouteCalculateResponse.builder()
                .origin(origin)
                .destination(dest)
                .paymentType(isIC ? "IC" : "TICKET")
                .routes(routes)
                .note(note)
                .build();
    }
}

package com.example.backend.service;

import com.example.backend.dto.RouteCalculateResponse;
import com.example.backend.dto.RouteCalculateResponse.RouteOption;
import com.example.backend.dto.SearchRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteCalculatorService {

    private final MultiRouteBuilder multiRouteBuilder;

    public RouteCalculatorService(MultiRouteBuilder multiRouteBuilder) {
        this.multiRouteBuilder = multiRouteBuilder;
    }

    public RouteCalculateResponse calculateRoute(SearchRequest request) {
        String origin = request.getOrigin();
        String dest = request.getDestination();
        String paymentType = request.getPaymentType() != null ? request.getPaymentType() : "IC";

        List<RouteOption> routes = multiRouteBuilder.buildMultiRoutes(origin, dest, paymentType);

        // 안내 문구
        String note = "도쿄 지하철 패스 보유시 [최저가] 경로 이용 시 100% 무료 혜택이 적용됩니다.";

        return RouteCalculateResponse.builder()
                .origin(origin)
                .destination(dest)
                .paymentType(paymentType)
                .routes(routes)
                .note(note)
                .build();
    }
}

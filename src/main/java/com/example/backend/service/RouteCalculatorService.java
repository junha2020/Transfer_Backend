package com.example.backend.service;

import com.example.backend.dto.RouteCalculateResponse;
import com.example.backend.dto.RouteCalculateResponse.RouteOption;
import com.example.backend.dto.SearchRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteCalculatorService {

    private final DirectRouteBuilder directRouteBuilder;
    private final TransferRouteBuilder transferRouteBuilder;

    public RouteCalculatorService(DirectRouteBuilder directRouteBuilder, TransferRouteBuilder transferRouteBuilder) {
        this.directRouteBuilder = directRouteBuilder;
        this.transferRouteBuilder = transferRouteBuilder;
    }

    public RouteCalculateResponse calculateRoute(SearchRequest request) {
        String origin = request.getOrigin();
        String dest = request.getDestination();
        boolean isIC = "IC".equalsIgnoreCase(request.getPaymentType());

        boolean hasPass = request.getPassId() != null && !request.getPassId().equalsIgnoreCase("none");

        // 한번에 갈 수 있는 경우
        List<RouteOption> routes = new ArrayList<>(directRouteBuilder.buildDirectRoutes(origin, dest, isIC, hasPass));

        // 환승해야 하는 경우
        if (routes.size() < 2) {
            routes.addAll(transferRouteBuilder.buildTransferRoutes(origin, dest, isIC, hasPass, 3, routes.size() + 1));
        }

        // 안내 문구
        String note = "도쿄 지하철 패스 보유시 [최저가] 경로 이용 시 100% 무료 혜택이 적용됩니다.";

        return RouteCalculateResponse.builder()
                .origin(origin)
                .destination(dest)
                .paymentType(isIC ? "IC" : "TICKET")
                .routes(routes)
                .note(note)
                .build();
    }
}

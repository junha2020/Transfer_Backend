package com.example.backend.service;

import com.example.backend.dto.RouteCalculateResponse;
import com.example.backend.dto.SearchRequest;
import org.springframework.stereotype.Service;

@Service
public class RouteCalculatorService {

    private final RegularOptionCalculator regularOptionCalculator;
    private final ExpressFeeCalculator expressFeeCalculator;
    
    public RouteCalculatorService(RegularOptionCalculator regularOptionCalculator, ExpressFeeCalculator expressFeeCalculator) {
        this.regularOptionCalculator = regularOptionCalculator;
        this.expressFeeCalculator = expressFeeCalculator;
    }

    public RouteCalculateResponse calculateRoute(SearchRequest request) {
        String origin = request.getOrigin();
        String dest = request.getDestination();

        // 옵션 1
        var regularOption = regularOptionCalculator.calculateRegularOption(origin, dest, request.getPaymentType());

        // 옵션 2
        var expressOption = expressFeeCalculator.calculateExpressOption(origin, dest, regularOption.getSavedAmount());

        // 안내 문구
        String note = (expressOption.getExpressSurcharge() > 0)
                ? "패스 적용 요금와 지정석 특급 열차 요금 중 선택할 수 있씁니다."
                : "도쿄 철도 노선 운임 계산이 완료되었습니다.";

        return RouteCalculateResponse.builder()
                .origin(origin)
                .destination(dest)
                .regularOption(regularOption)
                .expressOption(expressOption)
                .note(note)
                .build();
    }
}

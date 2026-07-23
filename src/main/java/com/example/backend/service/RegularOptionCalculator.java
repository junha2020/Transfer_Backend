package com.example.backend.service;

import com.example.backend.dto.RouteCalculateResponse.OptionInfo;
import org.springframework.stereotype.Component;

@Component
public class RegularOptionCalculator {

    private final HopCalculator hopCalculator;
    private final FareCalculatorFactory fareCalculatorFactory;

    public RegularOptionCalculator(HopCalculator hopCalculator, FareCalculatorFactory fareCalculatorFactory) {
        this.hopCalculator = hopCalculator;
        this.fareCalculatorFactory = fareCalculatorFactory;
    }

    public OptionInfo calculateRegularOption(String origin, String dest, String paymentType) {
        boolean isIC = "IC".equalsIgnoreCase(paymentType);

        // 노선 정보 동적 탐색
        HopCalculator.HopSearchResult routeInfo = hopCalculator.findRouteHopInfo(origin, dest);

        // 해당 회사 타입에 맞게 선택
        FareCalculator fareCalc = fareCalculatorFactory.getCalculator(routeInfo.getCompanyType());
        int calculatedFare = fareCalc.calculateFare(routeInfo.getHopCount(), isIC);

        // 지하철 패스 커버 노선 여부에 따른 결제액 및 절약액
        boolean isPassCovered = routeInfo.isPassCovered();;
        int baseFare = isPassCovered ? 0 : calculatedFare;
        int savedAmount = isPassCovered ? calculatedFare : 0;

        return OptionInfo.builder()
                .trainName(routeInfo.getLineName())
                .baseFare(baseFare)
                .expressSurcharge(0)
                .totalFare(baseFare)
                .savedAmount(savedAmount)
                .isPassApplied(isPassCovered)
                .build();
    }

}
package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCalculateResponse {

    private String origin;
    private String destination;
    private String paymentType;
    private List<RouteOption> routes;
    private String note;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteOption {
        private int routeNumber; // 몇 번째 루트인지
        private String trainName; // 어떤 노선인지
        private List<String> badges; // 루트에 해당하는 뱃지(최단거리, 최단시간, 최소금액)
        private int durationMinutes; // 소요시간
        private int transferCount; // 환승 횟수
        private int baseFare; // 기본 요금
        private int expressSurcharge; // 특급권 요금
        private int totalFare; // 총 결제 금액
        private int savedAmount; // 절약 금액
        private boolean isPassApplied; // 패스 적용 여부
        private List<SubwayLineData.StationData> intermediateStations;
    }
}

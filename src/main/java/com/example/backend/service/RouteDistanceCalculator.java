package com.example.backend.service;

import com.example.backend.dto.SubwayLineData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteDistanceCalculator {

    private final RouteDataDataLoader dataLoader;

    public RouteDistanceCalculator(RouteDataDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Getter
    @AllArgsConstructor
    public static class RouteDistanceResult {
        private double distanceKm; // 실제 영업거리
        private int travelTimeMinutes; // 실제 소요시간
        private String companyType;
        private boolean isPassCovered;
        private String lineName;
    }

    public RouteDistanceResult findRouteDistanceInfo(String origin, String dest) {
        for (SubwayLineData line : dataLoader.getSubwayLines()) {
            int originIdx = -1;
            int destIdx = -1;
            List<SubwayLineData.StationData> stations = line.getStations();

            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).getId().equalsIgnoreCase(origin)) originIdx = i;
                if (stations.get(i).getId().equalsIgnoreCase(dest)) destIdx = i;
            }

            // TODO: 일단 임시. 나중에 실제 역간 거리 넣어서 확인예정
            if (originIdx != -1 && destIdx != -1) {
                int hops = Math.abs(destIdx - originIdx);
                double distanceKm = hops * 1.8;
                int travelTime = (int)(hops * 3.0);

                return new RouteDistanceResult(distanceKm, travelTime, line.getType(), line.isPassCovered(), line.getLineName());
            }
        }

        return new RouteDistanceResult(5.0, 15, "SUBWAY", true, "도쿄메트로 마루노우치선"); // 에러 방지용
    }
}

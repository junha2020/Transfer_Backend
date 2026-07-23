package com.example.backend.service;

import com.example.backend.dto.SubwayLineData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HopCalculator {

    private final RouteDataDataLoader dataLoader;

    public HopCalculator(RouteDataDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Getter
    @AllArgsConstructor
    public static class HopSearchResult {
        private int hopCount;
        private String companyType;
        private boolean isPassCovered;
        private String lineName;
    }

    public HopSearchResult findRouteHopInfo(String origin, String dest) {
        for (SubwayLineData line : dataLoader.getSubwayLines()) {
            int originIdx = -1;
            int destIdx = -1;
            List<SubwayLineData.StationData> stations = line.getStations();

            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).getId().equalsIgnoreCase(origin)) originIdx = i;
                if (stations.get(i).getId().equalsIgnoreCase(dest)) destIdx = i;
            }

            if (originIdx != -1 && destIdx != -1) {
                int hops = Math.abs(destIdx - originIdx);
                return  new HopSearchResult(hops, line.getType(), line.isPassCovered(), line.getLineName());
            }
        }

        return new HopSearchResult(3, "SUBWAY", true, "일반 전철");
    }
}

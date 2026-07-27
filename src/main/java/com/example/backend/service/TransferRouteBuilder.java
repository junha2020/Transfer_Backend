package com.example.backend.service;

import com.example.backend.dto.RouteCalculateResponse.RouteOption;
import com.example.backend.dto.SubwayLineData;
import com.example.backend.service.strategy.FareCalculator;
import com.example.backend.service.strategy.FareCalculatorFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransferRouteBuilder {

    private final RouteDataDataLoader dataLoader;
    private final FareCalculatorFactory fareCalculatorFactory;

    public TransferRouteBuilder(RouteDataDataLoader dataLoader, FareCalculatorFactory fareCalculatorFactory) {
        this.dataLoader = dataLoader;
        this.fareCalculatorFactory = fareCalculatorFactory;
    }

    public List<RouteOption> buildTransferRoutes(String origin, String dest, boolean isIC, int startRouteNumber) {
        List<RouteOption> transferRoutes = new ArrayList<>();
        List<SubwayLineData> lines = dataLoader.getSubwayLines();

        for (SubwayLineData line1 : lines) {
            int originIdx = findStationIndex(line1, origin);
            if (originIdx == -1) continue;

            for (SubwayLineData line2 : lines) {
                if (line1.getLineId().equals(line2.getLineId())) continue;
                int destIdx = findStationIndex(line2, dest);
                if (destIdx == -1) continue;

                for (SubwayLineData.StationData transferStation : line1.getStations()) {
                    int transferOnLine2Idx = findStationIndex(line2, transferStation.getId());

                    if (transferOnLine2Idx != -1) {
                        int dist1 = Math.abs(findStationIndex(line1, transferStation.getId()) - originIdx);
                        int dist2 = Math.abs(destIdx - transferOnLine2Idx);

                        FareCalculator calc1 = fareCalculatorFactory.getCalculator(line1.getType());
                        FareCalculator calc2 = fareCalculatorFactory.getCalculator(line2.getType());
                        int fare1 = line1.isPassCovered() ? 0 : calc1.calculateFare(dist1 * 1.8, isIC);
                        int fare2 = line2.isPassCovered() ? 0 : calc2.calculateFare(dist2 * 1.8, isIC);
                        int totalFare = fare1 + fare2;

                        int duration1 = (int)(dist1 * 3.5);
                        int duration2 = (int)(dist2 * 3.5);
                        int totalDuration = duration1 + duration2 + 5;

                        boolean isPassApplied = line1.isPassCovered() && line2.isPassCovered();
                        List<String> badges = new ArrayList<>(List.of("FAST"));
                        if (isPassApplied) badges.add("CHEAP");

                        transferRoutes.add(RouteOption.builder()
                                .routeNumber(startRouteNumber++)
                                .trainName(line1.getLineName() + " ➔ " + line2.getLineName() + " (" + transferStation.getNameKor() + "환승)")
                                .badges(badges)
                                .durationMinutes(totalDuration)
                                .transferCount(1)
                                .baseFare(totalFare)
                                .expressSurcharge(0)
                                .totalFare(totalFare)
                                .savedAmount(isPassApplied ? 440 : 0)
                                .isPassApplied(isPassApplied)
                                .build());

                        if (transferRoutes.size() >= 3) break;
                    }
                }
            }
        }

        return transferRoutes;
    }

    private int findStationIndex(SubwayLineData line, String stationId) {
        List<SubwayLineData.StationData> stations = line.getStations();
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getId().equalsIgnoreCase(stationId)) return i;
        }
        return -1;
    }
}

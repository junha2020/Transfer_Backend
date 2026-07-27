package com.example.backend.service;

import com.example.backend.dto.RouteCalculateResponse.RouteOption;
import com.example.backend.dto.SubwayLineData;
import com.example.backend.service.strategy.FareCalculator;
import com.example.backend.service.strategy.FareCalculatorFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DirectRouteBuilder {

    private final RouteDataDataLoader dataLoader;
    private final FareCalculatorFactory fareCalculatorFactory;

    public DirectRouteBuilder(RouteDataDataLoader dataLoader, FareCalculatorFactory fareCalculatorFactory) {
        this.dataLoader = dataLoader;
        this.fareCalculatorFactory = fareCalculatorFactory;
    }

    public List<RouteOption> buildDirectRoutes(String origin, String dest, boolean isIC) {
        List<RouteOption> routes = new ArrayList<>();
        int routeCounter = 1;

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
                double distanceKm = hops * 1.8;

                FareCalculator calc = fareCalculatorFactory.getCalculator(line.getType());
                int calculatedFare = calc.calculateFare(distanceKm, isIC);

                boolean isPassCovered = line.isPassCovered();
                int baseFare = isPassCovered ? 0 : calculatedFare;
                int savedFare = isPassCovered ? calculatedFare : 0;

                double speedFactor = "JR".equalsIgnoreCase(line.getType()) ? 2.5 : (isPassCovered ? 3.5 : 4.0);
                int duration = Math.max(10, (int)(hops * speedFactor));

                List<String> badges = new ArrayList<>();
                if (isPassCovered) badges.add("CHEAP");
                badges.add("EASY");

                routes.add(RouteOption.builder()
                        .routeNumber(routeCounter++)
                        .trainName(line.getLineName())
                        .badges(badges)
                        .durationMinutes(duration)
                        .transferCount(0)
                        .baseFare(baseFare)
                        .expressSurcharge(0)
                        .totalFare(baseFare)
                        .savedAmount(savedFare)
                        .isPassApplied(isPassCovered)
                        .build());
            }
        }

        routes.stream()
                .min(Comparator.comparingInt(RouteOption::getDurationMinutes))
                .ifPresent(r -> {
                    if (!r.getBadges().contains("FAST")) r.getBadges().add("FAST");
                });

        return routes;
    }
}

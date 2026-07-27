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

    public List<RouteOption> buildDirectRoutes(String origin, String dest, boolean isIC, boolean hasPass) {
        List<RouteOption> routes = new ArrayList<>();
        int routeCounter = 1;

        for (SubwayLineData line : dataLoader.getSubwayLines()) {
            int originIdx = findStationIndex(line, origin);
            int destIdx = findStationIndex(line, dest);

            if (originIdx != -1 && destIdx != -1) {
                int hops = Math.abs(destIdx - originIdx);
                double distanceKm = hops * 1.8;

                FareCalculator calc = fareCalculatorFactory.getCalculator(line.getType());
                int calculatedFare = calc.calculateFare(distanceKm, isIC);

                boolean isPassCovered = hasPass && line.isPassCovered();
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

    private int findStationIndex(SubwayLineData line, String query) {
        if (query == null || query.trim().isEmpty()) return -1;
        String q = query.trim().toLowerCase();
        for (int i = 0; i < line.getStations().size(); i++) {
            var st = line.getStations().get(i);
            if (st.getId().equalsIgnoreCase(q) ||
                    (st.getNameKor() != null && st.getNameKor().equalsIgnoreCase(q)) ||
                    (st.getNameJpn() != null && st.getNameJpn().equalsIgnoreCase(q))) return i;
        }
        return -1;
    }
}

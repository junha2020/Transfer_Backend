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

    public List<RouteOption> buildTransferRoutes(String origin, String dest, boolean isIC, boolean hasPass, int startRouteNumber) {
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
                        if (dist1 == 0 || dist2 == 0) continue; // 자기 자신으로 환승하는 것 제외

                        FareCalculator calc1 = fareCalculatorFactory.getCalculator(line1.getType());
                        FareCalculator calc2 = fareCalculatorFactory.getCalculator(line2.getType());

                        // 패스 적용 안된 금액
                        int rawFare1 =  calc1.calculateFare(dist1 * 1.8, isIC);
                        int rawFare2 = calc2.calculateFare(dist2 * 1.8, isIC);
                        int originalTotalFare = rawFare1 + rawFare2;

                        // 패스 보유 여부 및 절약액 계산
                        boolean line1Covered = hasPass && line1.isPassCovered();
                        boolean line2Covered = hasPass && line2.isPassCovered();
                        int userPayFare1 = line1Covered ? 0 : rawFare1;
                        int userPayFare2 = line2Covered ? 0 : rawFare2;
                        int finalTotalFare = userPayFare1 + userPayFare2;

                        // 패스 절약액 계산
                        int savedAmount = originalTotalFare - finalTotalFare;
                        boolean isPassApplied= line1Covered || line2Covered;

                        int duration1 = (int)(dist1 * 3.5);
                        int duration2 = (int)(dist2 * 3.5);
                        int totalDuration = duration1 + duration2 + 5;

                        List<String> badges = new ArrayList<>(List.of("FAST"));
                        if (isPassApplied && finalTotalFare == 0) badges.add("CHEAP");

                        transferRoutes.add(RouteOption.builder()
                                .routeNumber(startRouteNumber++)
                                .trainName(line1.getLineName() + " ➔ " + line2.getLineName() + " (" + transferStation.getNameKor() + "환승)")
                                .badges(badges)
                                .durationMinutes(totalDuration)
                                .transferCount(1)
                                .baseFare(finalTotalFare)
                                .expressSurcharge(0)
                                .totalFare(finalTotalFare)
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

    private int findStationIndex(SubwayLineData line, String query) {
        if (query == null || query.trim().isEmpty()) return -1;
        String q = query.trim().toLowerCase();
        List<SubwayLineData.StationData> stations = line.getStations();
        for (int i = 0; i< stations.size(); i++) {
            SubwayLineData.StationData st = stations.get(i);
            if (st.getId().equalsIgnoreCase(q) ||
                    (st.getNameKor() != null && st.getNameKor().equalsIgnoreCase(q)) ||
                    (st.getNameJpn() != null && st.getNameJpn().equalsIgnoreCase(q))) {
                return i;
            }
        }
        return -1;
    }
}

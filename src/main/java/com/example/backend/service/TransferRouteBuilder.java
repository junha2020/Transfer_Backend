package com.example.backend.service;

import com.example.backend.dto.RouteCalculateResponse.RouteOption;
import com.example.backend.dto.SubwayLineData;
import com.example.backend.service.strategy.FareCalculator;
import com.example.backend.service.strategy.FareCalculatorFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TransferRouteBuilder {

    private final RouteDataDataLoader dataLoader;
    private final FareCalculatorFactory fareCalculatorFactory;

    public TransferRouteBuilder(RouteDataDataLoader dataLoader, FareCalculatorFactory fareCalculatorFactory) {
        this.dataLoader = dataLoader;
        this.fareCalculatorFactory = fareCalculatorFactory;
    }

    private static class PathNode {
        SubwayLineData currentLine;
        SubwayLineData.StationData currentStation;
        int transfers;
        double totalDistance;
        List<SubwayLineData.StationData> pathStations;
        List<String> lineSequence;
        List<String> transferStationNames;

        public PathNode(SubwayLineData currentLine, SubwayLineData.StationData currentStation, int transfers, double totalDistance, List<SubwayLineData.StationData> pathStations, List<String> lineSequence, List<String> transferStationNames) {
            this.currentLine = currentLine;
            this.currentStation = currentStation;
            this.transfers = transfers;
            this.totalDistance = totalDistance;
            this.pathStations = new ArrayList<>(pathStations);
            this.lineSequence = new ArrayList<>(lineSequence);
            this.transferStationNames = new ArrayList<>(transferStationNames);
        }
    }

    public List<RouteOption> buildTransferRoutes(String origin, String dest, boolean isIC, boolean hasPass, int maxTransfers, int startRouteNumber) {
        List<RouteOption> results = new ArrayList<>();
        List<SubwayLineData> lines = dataLoader.getSubwayLines();

        Queue<PathNode> queue = new LinkedList<>();

        for (SubwayLineData line : lines) {
            SubwayLineData.StationData originSt = findStation(line, origin);
            if (originSt != null) {
                queue.add(new PathNode(
                        line,
                        originSt,
                        0,
                        0.0,
                        List.of(originSt),
                        List.of(line.getLineName()),
                        new ArrayList<>()
                ));
            }
        }

        Set<String> visited = new HashSet<>();

        while (!queue.isEmpty() && results.size() < 5) {
            PathNode current = queue.poll();

            if (isSameStation(current.currentStation, dest)) {
                RouteOption option = createRouteOption(current, isIC, hasPass, startRouteNumber++);
                results.add(option);
                continue;
            }

            if (current.transfers >= maxTransfers) continue;

            String stateKey = current.currentLine.getLineId() + ":" + current.currentStation.getId();
            if (visited.contains(stateKey)) continue;
            visited.add(stateKey);

            List<SubwayLineData.StationData> lineStations = current.currentLine.getStations();
            int stIdx = lineStations.indexOf(current.currentStation);

            for (int nextIdx : new int[]{stIdx - 1, stIdx + 1}) {
                if (nextIdx >= 0 && nextIdx < lineStations.size()) {
                    SubwayLineData.StationData nextSt = lineStations.get(nextIdx);
                    List<SubwayLineData.StationData> newPath = new ArrayList<>(current.pathStations);
                    newPath.add(nextSt);

                    queue.add(new PathNode(
                            current.currentLine,
                            nextSt,
                            current.transfers,
                            current.totalDistance + 1.8,
                            newPath,
                            current.lineSequence,
                            current.transferStationNames
                    ));
                }
            }

            for (SubwayLineData nextLine : lines) {
                if (nextLine.getLineId().equals(current.currentLine.getLineId())) continue;
                SubwayLineData.StationData transferSt = findStation(nextLine, current.currentStation.getId());

                if (transferSt != null) {
                     List<String> newLines = new ArrayList<>(current.lineSequence);
                     newLines.add(nextLine.getLineName());
                     List<String> newTransfers = new ArrayList<>(current.transferStationNames);
                     newTransfers.add(current.currentStation.getNameKor());

                     queue.add(new PathNode(
                             nextLine,
                             transferSt,
                             current.transfers + 1,
                             current.totalDistance,
                             current.pathStations,
                             newLines,
                             newTransfers
                     ));
                }
            }
        }

        return results;
    }

    private RouteOption createRouteOption(PathNode node, boolean isIC, boolean hasPass, int routeNum) {
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < node.lineSequence.size(); i++) {
            nameBuilder.append(node.lineSequence.get(i));
            if (i < node.transferStationNames.size()) {
                nameBuilder.append(" ➔ (").append(node.transferStationNames.get(i)).append(" 환승) ");
            }
        }

        FareCalculator calc = fareCalculatorFactory.getCalculator(node.currentLine.getType());
        int originalFare = calc.calculateFare(node.totalDistance, isIC);
        int finalFare = hasPass ? 0 : originalFare;
        int saved = originalFare - finalFare;

        return RouteOption.builder()
                .routeNumber(routeNum)
                .trainName(nameBuilder.toString())
                .badges(List.of("FAST"))
                .durationMinutes((int)(node.totalDistance * 2.0) + (node.transfers * 5))
                .transferCount(node.transfers)
                .baseFare(finalFare)
                .expressSurcharge(0)
                .totalFare(finalFare)
                .savedAmount(saved)
                .isPassApplied(hasPass)
                .intermediateStations(node.pathStations)
                .build();
    }

    private SubwayLineData.StationData findStation(SubwayLineData line, String name) {
        for (SubwayLineData.StationData st : line.getStations()) {
            if (st.getId().equalsIgnoreCase(name) || (st.getNameKor() != null && st.getNameKor().equalsIgnoreCase(name))) {
                return st;
            }
        }
        return null;
    }

    private boolean isSameStation(SubwayLineData.StationData st, String name) {
        return st.getId().equalsIgnoreCase(name) || (st.getNameKor() != null & st.getNameKor().equalsIgnoreCase(name));
    }
}

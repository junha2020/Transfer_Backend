package com.example.backend.service;

import com.example.backend.dto.ExpressFeeData;
import com.example.backend.dto.RouteCalculateResponse;
import com.example.backend.dto.SearchRequest;
import com.example.backend.dto.SubwayLineData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteCalculatorService {

    private final RouteDataDataLoader dataLoader;

    public RouteCalculatorService(RouteDataDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public RouteCalculateResponse calculateRoute(SearchRequest request) {
        String origin = request.getOrigin();
        String dest = request.getDestination();

        // 역 간 간격 계싼
        int hopCount = calculateHopCount(origin, dest);

        // 패스 미사용 시 일반 운임
        int standardFare = calculateDistanceFare(hopCount, "SUBWAY");

        // 옵션 1. 일반 전철/지하철 이용 시
        RouteCalculateResponse.OptionInfo regularOption =
                RouteCalculateResponse.OptionInfo.builder()
                        .trainName("일반 전철")
                        .baseFare(0)
                        .expressSurcharge(0)
                        .totalFare(0)
                        .savedAmount(standardFare)
                        .isPassApplied(true)
                        .build();

        // 옵션 2. 지정석 특급 열차 이용 시
        RouteCalculateResponse.OptionInfo expressOption = null;

        for (ExpressFeeData expressData : dataLoader.getExpressFees()) {
            for (ExpressFeeData.FareMatrixItem item : expressData.getFareMatrix()) {
                // 양방향 특급 검색
                if ((item.getDepartureStationId().equalsIgnoreCase(origin) && item.getArrivalStationId().equalsIgnoreCase(dest))
                || (item.getArrivalStationId().equalsIgnoreCase(dest) && item.getArrivalStationId().equalsIgnoreCase(origin))) {
                    int privateFare = 0;
                    if (dest.contains("hakone") || origin.contains("hakone")) privateFare = 1060;
                    else if (dest.contains("nikko") || origin.contains("nikko")) privateFare = 1400;
                    else if (dest.contains("narita") || origin.contains("narita")) privateFare = 1350;

                    if (expressOption == null) {
                        expressOption = RouteCalculateResponse.OptionInfo.builder()
                                .trainName("도쿄 메트로 / 도영 지하철 일반 전철")
                                .baseFare(privateFare)
                                .expressSurcharge(item.getExpressSurcharge())
                                .totalFare(privateFare + item.getExpressSurcharge())
                                .savedAmount(standardFare)
                                .isPassApplied(true)
                                .build();
                    }
                    break;
                }
            }
        }

        String note = (expressOption != null)
                ? "일반 전철과 지정석 특급 열차 중 선택이 가능합니다."
                : "도쿄 지하철 패스로 지하철 구간 전액 무료 혜택이 적용됩니다.";

        return RouteCalculateResponse.builder()
                .origin(origin)
                .destination(dest)
                .regularOption(regularOption)
                .expressOption(expressOption)
                .note(note)
                .build();
    }

    public int calculateDistanceFare(int hopCount, String companyType) {
        if ("JR".equalsIgnoreCase(companyType)) {
            if (hopCount <= 2) return 150;
            else if (hopCount <= 4) return 170;
            else if (hopCount <= 7) return 180;
            else return 230;
        } else {
            if (hopCount <= 2) return 180;
            else if (hopCount <= 4) return 210;
            else if (hopCount <= 7) return 260;
            else return 300;
        }
    }

    private int calculateHopCount(String origin, String dest) {
        for (SubwayLineData line : dataLoader.getSubwayLines()) {
            int originIdx = -1;
            int destIdx = -1;
            List<SubwayLineData.StationData> stations = line.getStations();

            for (int i = 0; i< stations.size(); i++) {
                if (stations.get(i).getId().equalsIgnoreCase(origin)) originIdx = i;
                if (stations.get(i).getId().equalsIgnoreCase(dest)) destIdx = i;
            }

            if (originIdx != -1 && destIdx != -1) {
                return Math.abs(destIdx - originIdx);
            }
        }
        return  3;
    }
}

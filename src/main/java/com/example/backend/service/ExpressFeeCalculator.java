package com.example.backend.service;

import com.example.backend.dto.ExpressFeeData;
import com.example.backend.dto.RouteCalculateResponse.OptionInfo;
import org.springframework.stereotype.Component;

@Component
public class ExpressFeeCalculator {

    private final RouteDataDataLoader dataLoader;

    public ExpressFeeCalculator(RouteDataDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public OptionInfo calculateExpressOption(String origin, String dest, int savedFare) {
        for (ExpressFeeData expressData : dataLoader.getExpressFees()) {
            for (ExpressFeeData.FareMatrixItem item : expressData.getFareMatrix()) {
                if ((item.getDepartureStationId().equalsIgnoreCase(origin) && item.getArrivalStationId().equalsIgnoreCase(dest))
                || (item.getDepartureStationId().equalsIgnoreCase(dest) && item.getArrivalStationId().equalsIgnoreCase(origin))) {
                    int privateFare = getPrivateLineBaseFare(origin, dest);

                    return OptionInfo.builder()
                            .trainName(expressData.getTrainName())
                            .baseFare(privateFare)
                            .expressSurcharge(item.getExpressSurcharge())
                            .totalFare(privateFare + item.getExpressSurcharge())
                            .savedAmount(savedFare)
                            .isPassApplied(true)
                            .build();
                }
            }
        }

        return OptionInfo.builder()
                .trainName("일반 열차")
                .baseFare(0)
                .expressSurcharge(0)
                .totalFare(0)
                .savedAmount(0)
                .isPassApplied(false)
                .build();
    }

    private int getPrivateLineBaseFare(String origin, String dest) {
        if (dest.contains("hakone") || origin.contains("hakone")) return 1060;
        if (dest.contains("nikko") || origin.contains("nikko")) return 1400;
        if (dest.contains("narita") || origin.contains("narita")) return 1350;
        return 0;
    }
}

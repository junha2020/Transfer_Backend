package com.example.backend.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class TokyoMetroFareCalculator implements FareCalculator {

    @Override
    public boolean supports(String companyType) {
        return companyType == null || "TOKYO_METRO".equalsIgnoreCase(companyType) ||
                "METRO".equalsIgnoreCase(companyType) || "SUBWAY".equalsIgnoreCase(companyType);
    }

    @Override
    public int calculateFare(double distanceKm, boolean isIC) {
        if (distanceKm <= 6.0) return isIC ? 178 : 180;
        else if (distanceKm <= 11.0) return isIC ? 209 : 210;
        else if (distanceKm <= 19.0) return isIC ? 252 : 260;
        else if (distanceKm <= 27.0) return isIC ? 293 : 300;
        else return isIC ? 324 : 330;
    }
}

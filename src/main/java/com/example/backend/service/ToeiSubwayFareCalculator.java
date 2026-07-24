package com.example.backend.service;

import org.springframework.stereotype.Component;

@Component
public class ToeiSubwayFareCalculator implements FareCalculator {

    @Override
    public boolean supports(String companyType) {
        return "TOEI".equalsIgnoreCase(companyType);
    }

    @Override
    public int calculateFare(double distanceKm, boolean isIC) {
        if (distanceKm <= 4.0) return isIC ? 178 : 180;
        else if (distanceKm <= 9.0) return 220;
        else if (distanceKm <= 15.0) return isIC ? 272 : 280;
        else if (distanceKm <= 21.0) return isIC ? 325 : 330;
        else return isIC ? 377 : 380;
    }
}

package com.example.backend.service;

import org.springframework.stereotype.Component;

@Component
public class JrFareCalculator implements FareCalculator {

    @Override
    public boolean supports(String companyType) {
        return "JR".equalsIgnoreCase(companyType);
    }

    @Override
    public int calculateFare(double distanceKm, boolean isIC) {
        if (distanceKm <= 3.0) return isIC ? 155 : 160;
        else if (distanceKm <= 6.0) return isIC ? 199 : 200;
        else if (distanceKm <= 10.0) return isIC ? 209 : 210;
        else if (distanceKm <= 15.0) return isIC ? 253 : 260;
        else if (distanceKm <= 20.0) return isIC ? 341 : 350;
        else if (distanceKm <= 25.0) return isIC ? 440 : 440;
        else if (distanceKm <= 30.0) return isIC ? 528 : 530;
        else if (distanceKm <= 35.0) return isIC ? 616 : 620;
        else if (distanceKm <= 40.0) return isIC ? 715 : 720;
        else if (distanceKm <= 45.0) return isIC ? 803 : 810;
        else if (distanceKm <= 50.0) return isIC ? 902 : 910;
        else if (distanceKm <= 60.0) return isIC ? 1034 : 1040;
        else if (distanceKm <= 70.0) return isIC ? 1221 : 1230;
        else if (distanceKm <= 80.0) return isIC ? 1408 : 1410;
        else if (distanceKm <= 90.0) return isIC ? 1595 : 1600;
        else if (distanceKm <= 100.0) return isIC ? 1782 : 1790;
        else if (distanceKm <= 120.0) return isIC ? 2090 : 2090;
        else if (distanceKm <= 140.0) return isIC ? 2420 : 2420;
        else return isIC ? 2750 : 2750;
    }
}

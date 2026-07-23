package com.example.backend.service;

import org.springframework.stereotype.Component;

@Component
public class ToeiSubwayFareCalculator implements FareCalculator {

    @Override
    public boolean supports(String companyType) {
        return "TOEI".equalsIgnoreCase(companyType);
    }

    @Override
    public int calculateFare(int hopCount, boolean isIC) {
        if (hopCount <= 2) return isIC ? 178 : 180;
        else if (hopCount <= 4) return 220;
        else if (hopCount <= 7) return isIC ? 272 : 280;
        else if (hopCount <= 10) return isIC ? 325 : 330;
        else return isIC ? 377 : 380;
    }
}

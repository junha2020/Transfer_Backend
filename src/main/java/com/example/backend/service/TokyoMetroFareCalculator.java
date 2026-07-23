package com.example.backend.service;

import org.springframework.stereotype.Component;

@Component
public class TokyoMetroFareCalculator implements FareCalculator {

    @Override
    public boolean supports(String companyType) {
        return companyType == null || "TOKYO_METRO".equalsIgnoreCase(companyType) ||
                "METRO".equalsIgnoreCase(companyType) || "SUBWAY".equalsIgnoreCase(companyType);
    }

    @Override
    public int calculateFare(int hopCount, boolean isIC) {
        if (hopCount <= 2) return isIC ? 178 : 180;
        else if (hopCount <= 4) return isIC ? 209 : 210;
        else if (hopCount <= 7) return isIC ? 252 : 260;
        else if (hopCount <= 10) return isIC ? 293 : 300;
        else return isIC ? 324 : 330;
    }
}

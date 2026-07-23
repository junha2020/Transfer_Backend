package com.example.backend.service;

import org.springframework.stereotype.Component;

@Component
public class JrFareCalculator implements FareCalculator {

    @Override
    public boolean supports(String companyType) {
        return "JR".equalsIgnoreCase(companyType);
    }

    @Override
    public int calculateFare(int hopCount, boolean isIC) {
        if (hopCount <= 2) return isIC ? 155 : 160;
        else if (hopCount <= 4) return isIC ? 199 : 200;
        else if (hopCount <= 7) return isIC ? 209 : 210;
        else return isIC ? 253 : 260;
    }
}

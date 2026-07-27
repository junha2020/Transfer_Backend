package com.example.backend.service.strategy;

public interface FareCalculator {

    boolean supports(String companyType);
    int calculateFare(double distanceKm, boolean isIC);
}

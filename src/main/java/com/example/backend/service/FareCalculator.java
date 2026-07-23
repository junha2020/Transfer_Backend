package com.example.backend.service;

public interface FareCalculator {

    boolean supports(String companyType);
    int calculateFare(int hopCount, boolean isIC);
}

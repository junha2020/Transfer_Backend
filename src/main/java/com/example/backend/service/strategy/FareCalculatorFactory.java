package com.example.backend.service.strategy;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FareCalculatorFactory {

    private final List<FareCalculator> calculators;

    public FareCalculatorFactory(List<FareCalculator> calculators) {
        this.calculators = calculators;
    }

    public FareCalculator getCalculator(String companyType) {
        return calculators.stream()
                .filter(c -> c.supports(companyType))
                .findFirst()
                .orElse(calculators.get(0));
    }
}

package com.example.backend.dto;

import lombok.Data;

@Data
public class SearchRequest {

    private String origin;
    private String destination;
    private String passId;
    private String paymentType; // 교통카드냐 티켓이냐
}

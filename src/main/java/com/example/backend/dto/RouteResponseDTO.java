package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RouteResponseDTO {
    private int originTotalFare; // 원본 총 요금
    private int discountedTotalFare; // 패스 적용 후 총 요금
    private int savedAmount; // 할인된 금액 (이득 본 금액)
    private List<StepDTO> steps; // 상세 경로 리스트

    @Data
    @Builder
    public static class StepDTO {
        private String agency; // 운영사
        private int fare; // 이 구간 요금
        private boolean isDiscountApplied; // 패스 적용 여부
    }
}

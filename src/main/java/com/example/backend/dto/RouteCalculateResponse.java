package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCalculateResponse {

    private String origin;
    private String destination;
    private OptionInfo regularOption; // 일반 전철 및 지하철 이용 시
    private OptionInfo expressOption; // 특급열차 이용 시
    private String note; // 안내문구

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionInfo {
        private String trainName; // 열차 이름
        private int baseFare; //패스 적용 실제 기본 금액
        private int expressSurcharge; // 특급권 금액
        private int totalFare; // 총 금액
        private int savedAmount; // 지하철 패스로 절약한 금액
        private boolean isPassApplied; // 패스 혜택 적용 여부
    }
}

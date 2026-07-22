package com.example.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExpressFeeData {

    private String lineId;
    private String trainName;
    private String note;
    private List<FareMatrixItem> fareMatrix;

    @Data
    public static class FareMatrixItem {
        private String departureStationId;
        private String arrivalStationId;
        private int expressSurcharge;
    }
}

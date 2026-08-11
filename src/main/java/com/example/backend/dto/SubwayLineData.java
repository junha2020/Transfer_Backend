package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SubwayLineData {

    private String lineId;
    private String lineName;
    private String type; // SUBWAY, JR, PRIVATE

    @JsonProperty("isPassCovered")
    private boolean isPassCovered;

    private List<StationData> stations;

    @Data
    public static class StationData {
        private String id;
        private String nameKor;
        private String nameJpn;
        private boolean isBoundary;
    }
}

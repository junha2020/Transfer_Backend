package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.sound.sampled.Line;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleDirectionsResponse {
    private List<Route> routes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        private List<Leg> legs;
        private Fare fare;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Leg {
        @JsonProperty("departure_time")
        private TimeInfo departureTime;

        @JsonProperty("arrival_time")
        private TimeInfo arrivalTime;

        private TextValue duration;
        private List<Step> steps;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Step {
        @JsonProperty("travel_mode")
        private String travelMode; // "Transit"(대중교통), "Walking"(도보) 등

        @JsonProperty("transit_details")
        private TransitDetails transitDetails;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitDetails {
        @JsonProperty("departure_stop")
        private Stop departureStop;

        @JsonProperty("arrival_stop")
        private Stop arrivalStop;

        @JsonProperty("num_stop")
        private int Stops;

        private Line line;
    }

    // 나머지 기타 데이터용
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fare {
        private String text; // "253엔"
        private int value; // 253
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeInfo {
        private String text; // "22:13"
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TextValue {
        private String text; // "13분"
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stop {
        private String name; // "도쿄역"
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Line {
        private String name; // "JR 츄오선 쾌속"
        @JsonProperty("short_name")
        private String shortName;
    }
}

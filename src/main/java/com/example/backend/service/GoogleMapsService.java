package com.example.backend.service;

import com.example.backend.dto.GoogleDirectionsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoogleMapsService {

    private final RestTemplate restTemplate;

    // yml에 설정한 google.maps.api-key 값 주입
    @Value("${google.maps.api-key}")
    private String apiKey;

    public GoogleMapsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GoogleDirectionsResponse getDirections(String origin, String destination) {
        String baseUrl = "https://maps.googleapis.com/maps/api/directions/json";

        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("mode", "transit")
                .queryParam("key", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, GoogleDirectionsResponse.class);
    }
}

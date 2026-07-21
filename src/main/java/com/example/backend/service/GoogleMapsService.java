package com.example.backend.service;

import com.example.backend.dto.GoogleDirectionsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

@Service
public class GoogleMapsService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // yml에 설정한 google.maps.api-key 값 주입
    @Value("${google.maps.api-key}")
    private String apiKey;

    public GoogleMapsService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public GoogleDirectionsResponse getDirections(String origin, String destination) {
        String baseUrl = "https://maps.googleapis.com/maps/api/directions/json";

        java.net.URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("mode", "transit")
                .queryParam("language", "ja")
                .queryParam("region", "jp")
                .queryParam("key", apiKey)
                .build()
                .encode()
                .toUri();

        System.out.println("🚨 [디버깅] 구글 API 요청 URL 🚨 : " + uri.toString());

        try {
            String rawJson = restTemplate.getForObject(uri, String.class);
            System.out.println("🚨 [구글 찐 응답 JSON 원본] 🚨 \n" + rawJson);

            return objectMapper.readValue(rawJson, GoogleDirectionsResponse.class);
        } catch (Exception e) {
            System.out.println("JSON 파싱 에러: " + e.getMessage());
            return new GoogleDirectionsResponse();
        }
    }
}

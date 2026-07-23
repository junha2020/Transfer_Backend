package com.example.backend.service;

import com.example.backend.dto.ExpressFeeData;
import com.example.backend.dto.SubwayLineData;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Component
@Getter
public class RouteDataDataLoader {

    private List<SubwayLineData> subwayLines;
    private List<ExpressFeeData> expressFees;

    @PostConstruct
    public void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // subway_lines.json 로드
            InputStream linesStream = new ClassPathResource("data/subway_lines.json").getInputStream();
            subwayLines = objectMapper.readValue(linesStream, new TypeReference<List<SubwayLineData>>() {});

            // express_fees.json 로드
            InputStream expressStream = new ClassPathResource("data/express_fees.json").getInputStream();
            expressFees = objectMapper.readValue(expressStream, new TypeReference<List<ExpressFeeData>>() {});

            System.out.println("도쿄 철도 & 특급 데이터 로드 완료.");
        } catch (Exception e) {
            System.out.println("데이터 로드 중 에러 발생: " + e.getMessage());

        }
    }
}

package com.example.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("[테스트 1] 지하철 패스 100% 공제 노선 검색(IC)")
    void testCalculateRoute_Subway_IC() throws Exception {
        String jsonRequest = """
            {
                "origin": "shinjuku",
                "destination": "ginza",
                "passId": "tokyo_subway_ticket",
                "paymentType": "IC"
            }
            """;

        mockMvc.perform(post("/api/v1/routes/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("shinjuku"))
                .andExpect(jsonPath("$.destination").value("ginza"))
                .andExpect(jsonPath("$.routes[0]").exists());
    }

    @Test
    @DisplayName("[테스트 2] JR 야마노테선 탑승 시 (지하철 패스 미적용)")
    void testCalculateRoute_JR() throws Exception {
        String jsonRequest = """
            {
                "origin": "shinjuku",
                "destination": "tokyo",
                "passId": "tokyo_subway_ticket",
                "paymentType": "IC"
            }
        """;

        mockMvc.perform(post("/api/v1/routes/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routes[0].passApplied").value(false));
    }
}

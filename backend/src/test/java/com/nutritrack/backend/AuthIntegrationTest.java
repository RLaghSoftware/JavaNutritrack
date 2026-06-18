package com.nutritrack.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signupLoginAndAccessProtectedDashboard() throws Exception {
        String signupBody = """
                {"username":"testuser","email":"test@example.com","password":"password123"}
                """;

        MvcResult signupResult = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        JsonNode json = objectMapper.readTree(signupResult.getResponse().getContentAsString());
        String token = json.get("token").asText();

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void addMealAndGetMealsInRange() throws Exception {
        String signupBody = """
                {"username":"mealuser","email":"meal@example.com","password":"password123"}
                """;

        MvcResult signupResult = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupBody))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(signupResult.getResponse().getContentAsString())
                .get("token").asText();

        String mealBody = """
                {"mealName":"Chicken salad","date":"2026-05-15","protein":35,"carbs":20,"fat":12,"calories":380}
                """;

        mockMvc.perform(post("/api/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mealBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/meals")
                        .param("startDate", "2026-05-01")
                        .param("endDate", "2026-05-31")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].mealName").value("Chicken salad"));
    }

    @Test
    void getMetricsReportAndDownload() throws Exception {
        String signupBody = """
                {"username":"metricsuser","email":"metrics@example.com","password":"password123"}
                """;

        MvcResult signupResult = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupBody))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(signupResult.getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(get("/api/metrics/report")
                        .param("startDate", "2026-05-01")
                        .param("endDate", "2026-05-03")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows.length()").value(3))
                .andExpect(jsonPath("$.totals.protein").exists())
                .andExpect(jsonPath("$.totals.calories").exists());

        MvcResult downloadResult = mockMvc.perform(get("/api/metrics/report/download")
                        .param("startDate", "2026-05-01")
                        .param("endDate", "2026-05-03")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String contentType = downloadResult.getResponse().getContentType();
        if (contentType != null) {
            assertTrue(contentType.contains("spreadsheetml"));
        }
        assertTrue(downloadResult.getResponse().getContentAsByteArray().length > 0);
    }
}

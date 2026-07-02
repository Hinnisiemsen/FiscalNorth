package de.fiscalnorth.goal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fiscalnorth.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GoalIntegrationTest extends IntegrationTestBase {

    @Test
    void goals_crudAndInterviewFlow() throws Exception {
        RequestPostProcessor user = authenticatedUser();

        mockMvc.perform(get("/api/goals").with(user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(post("/api/goals")
                        .with(user)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Emergency fund",
                                  "goalType": "EMERGENCY_FUND",
                                  "targetAmount": 5000,
                                  "currentAmount": 1000,
                                  "monthlyContribution": 200
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Emergency fund")))
                .andExpect(jsonPath("$.progressPercent", is(20.0)));

        mockMvc.perform(get("/api/goals/overview").with(user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGoals", is(1)))
                .andExpect(jsonPath("$.activeGoals", is(1)));

        MvcResult interviewResult = mockMvc.perform(post("/api/goals/interview/start")
                        .with(user)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andReturn();

        JsonNode sessionJson = new ObjectMapper().readTree(interviewResult.getResponse().getContentAsString());
        long interviewSessionId = sessionJson.get("id").asLong();

        mockMvc.perform(put("/api/goals/interview/" + interviewSessionId + "/answers")
                        .with(user)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "answers": {
                                    "priorities": ["VACATION"],
                                    "targets": {"VACATION": 3000},
                                    "monthlyWillingToSave": 150
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/goals/interview/" + interviewSessionId + "/generate-plan")
                        .with(user)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedGoals", not(empty())))
                .andExpect(jsonPath("$.summary", not(emptyString())));

        mockMvc.perform(post("/api/goals/interview/" + interviewSessionId + "/apply-plan")
                        .with(user)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "goals": [
                                    {
                                      "name": "Summer vacation",
                                      "goalType": "VACATION",
                                      "targetAmount": 3000,
                                      "monthlyContribution": 150,
                                      "rationale": "Test goal"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/goals").with(user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}

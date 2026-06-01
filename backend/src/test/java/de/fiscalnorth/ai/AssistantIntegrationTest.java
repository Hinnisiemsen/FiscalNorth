package de.fiscalnorth.ai;

import de.fiscalnorth.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AssistantIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void status_whenAiDisabled_reportsUnavailable() throws Exception {
        mockMvc.perform(get("/api/assistant/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)));
    }
}

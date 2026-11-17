package de.fiscalnorth.ai;

import de.fiscalnorth.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "app.ai.enabled=true",
        "app.ai.demo-fallback=true",
        "spring.ai.google.genai.api-key="
})
class AssistantDemoIntegrationTest extends IntegrationTestBase {

    @Test
    void status_withoutApiKey_reportsDemoAvailable() throws Exception {
        mockMvc.perform(get("/api/assistant/status").with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.demo", is(true)))
                .andExpect(jsonPath("$.message", containsString("Demo")));
    }

    @Test
    void chat_withoutApiKey_returnsDemoReplyInsteadOf503() throws Exception {
        var user = authenticatedUser();
        mockMvc.perform(post("/api/assistant/chat")
                        .with(user)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"How much did I spend on groceries this month?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", not("")))
                .andExpect(jsonPath("$.reply", containsString("267")));
    }

    @Test
    void chat_budgetExample_returnsProposedAction() throws Exception {
        var user = authenticatedUser();
        mockMvc.perform(post("/api/assistant/chat")
                        .with(user)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Create a transport budget.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proposedActions[0].type", is("CREATE_BUDGET")));
    }
}

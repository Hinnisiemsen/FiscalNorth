package de.fiscalnorth.shared;

import de.fiscalnorth.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class I18nIntegrationTest extends IntegrationTestBase {

    @Test
    void notFound_returnsFrenchMessageWhenAcceptLanguageIsFr() throws Exception {
        mockMvc.perform(get("/api/goals/999999")
                        .with(authenticatedUser())
                        .header("Accept-Language", "fr"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("introuvable")));
    }
}

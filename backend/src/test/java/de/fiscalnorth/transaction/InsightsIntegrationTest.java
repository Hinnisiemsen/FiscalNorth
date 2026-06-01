package de.fiscalnorth.transaction;

import de.fiscalnorth.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InsightsIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void insights_afterCsvImport_returnsCategorySpending() throws Exception {
        String csv = """
            Auftragskonto;Buchungstag;Valutadatum;Buchungstext;Verwendungszweck;Beguenstigter/Zahlungspflichtiger;Betrag;Waehrung
            DE1;02.06.2026;02.06.2026;LASTSCHRIFT;REWE;REWE;87,40;EUR
            """;
        mockMvc.perform(multipart("/api/transaction/import/csv")
                        .file(new MockMultipartFile(
                                "file", "insights.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8)))
                        .param("preset", "SPARKASSE"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/insights").param("year", "2026").param("month", "6"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.periodStart", is("2026-06-01")))
                .andExpect(jsonPath("$.spendingByCategory", not(empty())))
                .andExpect(jsonPath("$.spendingByCategory[0].amount", greaterThan(0)));
    }
}

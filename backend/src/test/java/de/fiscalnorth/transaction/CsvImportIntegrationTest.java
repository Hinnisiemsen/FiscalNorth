package de.fiscalnorth.transaction;

import de.fiscalnorth.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CsvImportIntegrationTest extends IntegrationTestBase {

    @Test
    void importCsv_SparkasseFormat_returnsImportedCount() throws Exception {
        String csvContent = """
            Auftragskonto;Buchungstag;Valutadatum;Buchungstext;Verwendungszweck;Beguenstigter/Zahlungspflichtiger;Betrag;Waehrung
            DE1234567890;01.11.2024;01.11.2024;LASTSCHRIFT;REWE;REWE;50,00;EUR
            DE1234567890;15.11.2024;15.11.2024;LASTSCHRIFT;Netflix;Netflix;15,00;EUR
            DE1234567890;25.11.2024;25.11.2024;GUTSCHRIFT;Gehalt November;Arbeitgeber GmbH;3500,00;EUR
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes());

        mockMvc.perform(multipart("/api/transaction/import/csv")
                        .file(file)
                        .param("preset", "SPARKASSE")
                        .with(authenticatedUser())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.skippedDuplicates", any(Integer.class)));
    }

    @Test
    void importCsv_secondImport_skipsDuplicates() throws Exception {
        var user = authenticatedUser();
        String csvContent = """
            Auftragskonto;Buchungstag;Valutadatum;Buchungstext;Verwendungszweck;Beguenstigter/Zahlungspflichtiger;Betrag;Waehrung
            DE1234567890;01.12.2024;01.12.2024;LASTSCHRIFT;Test Duplicate;Test;10,00;EUR
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "dup.csv",
                "text/csv",
                csvContent.getBytes());

        mockMvc.perform(multipart("/api/transaction/import/csv")
                        .file(file)
                        .param("preset", "SPARKASSE")
                        .with(user)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", greaterThanOrEqualTo(1)));

        MockMultipartFile file2 = new MockMultipartFile(
                "file", "dup2.csv", "text/csv", csvContent.getBytes());
        mockMvc.perform(multipart("/api/transaction/import/csv")
                        .file(file2)
                        .param("preset", "SPARKASSE")
                        .with(user)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", equalTo(0)))
                .andExpect(jsonPath("$.skippedDuplicates", greaterThanOrEqualTo(1)));
    }
}

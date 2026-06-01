package de.fiscalnorth.transaction.service;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.transaction.dto.CsvImportResult;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.model.TransactionType;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvImportServiceTest {

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private CsvImportService service;

    @BeforeEach
    void setUp() {
        service = new CsvImportService(paymentTransactionRepository, categoryRepository);
    }

    @Test
    void importFromCsv_parsesGermanAmountsAndSavesTransactions() {
        String csv = """
            Auftragskonto;Buchungstag;Valutadatum;Buchungstext;Verwendungszweck;Beguenstigter/Zahlungspflichtiger;Betrag;Waehrung
            DE1;01.11.2024;01.11.2024;LASTSCHRIFT;REWE;REWE;-50,00;EUR
            DE1;25.11.2024;25.11.2024;GUTSCHRIFT;Gehalt;Arbeitgeber;3.500,00;EUR
            """;
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        when(paymentTransactionRepository.existsByImportHash(anyString())).thenReturn(false);
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CsvImportResult result = service.importFromCsv(file, CsvImportService.BankPreset.SPARKASSE);

        assertThat(result.imported()).isEqualTo(2);
        assertThat(result.skippedDuplicates()).isZero();
        assertThat(result.errorMessages()).isEmpty();

        ArgumentCaptor<PaymentTransaction> captor = ArgumentCaptor.forClass(PaymentTransaction.class);
        verify(paymentTransactionRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(PaymentTransaction::getTransactionType)
                .containsExactly(TransactionType.Expense, TransactionType.Income);
    }

    @Test
    void importFromCsv_skipsDuplicateHashes() {
        String csv = """
            Auftragskonto;Buchungstag;Valutadatum;Buchungstext;Verwendungszweck;Beguenstigter/Zahlungspflichtiger;Betrag;Waehrung
            DE1;01.12.2024;01.12.2024;LASTSCHRIFT;Test;Shop;10,00;EUR
            """;
        MockMultipartFile file = new MockMultipartFile(
                "file", "dup.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        when(paymentTransactionRepository.existsByImportHash(anyString())).thenReturn(true);

        CsvImportResult result = service.importFromCsv(file, CsvImportService.BankPreset.SPARKASSE);

        assertThat(result.imported()).isZero();
        assertThat(result.skippedDuplicates()).isEqualTo(1);
        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    void importFromCsv_emptyFile_returnsError() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        CsvImportResult result = service.importFromCsv(file, CsvImportService.BankPreset.SPARKASSE);

        assertThat(result.imported()).isZero();
        assertThat(result.errorMessages()).isNotEmpty();
    }

}

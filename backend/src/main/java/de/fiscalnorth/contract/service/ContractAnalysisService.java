package de.fiscalnorth.contract.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fiscalnorth.ai.client.GeminiClient;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.contract.dto.ContractDocumentAnalysisResult;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.model.ContractInterval;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.shared.LocalizedException;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractAnalysisService {

    private static final String DOCUMENT_SYSTEM_PROMPT = """
            You extract recurring payment contracts from financial documents.
            Respond with ONLY a JSON array (no markdown). Each item:
            {"name":"string","amount":number,"interval":"MONTHLY|QUARTERLY|YEARLY","startDate":"YYYY-MM-DD or null"}
            If none found, return [].
            """;

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ContractRepository contractRepository;
    private final CurrentUserService currentUserService;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.enabled:true}")
    private boolean aiEnabled;

    @Transactional
    public int analyzeAndCreateContracts() {
        User owner = currentUserService.getCurrentUser();
        List<PaymentTransaction> allTransactions =
                paymentTransactionRepository.findAllByOwnerId(owner.getId());
        int createdContracts = 0;

        Map<String, Map<Double, List<PaymentTransaction>>> groupedTransactions = allTransactions.stream()
                .filter(t -> t.getDescription() != null && t.getAmount() != null)
                .collect(Collectors.groupingBy(PaymentTransaction::getDescription,
                        Collectors.groupingBy(t -> t.getAmount().doubleValue())));

        for (var entry : groupedTransactions.entrySet()) {
            String description = entry.getKey();
            for (var amountEntry : entry.getValue().entrySet()) {
                List<PaymentTransaction> transactions = amountEntry.getValue();

                if (transactions.size() >= 3) {
                    boolean exists = contractRepository.findAllByOwnerId(owner.getId()).stream()
                            .anyMatch(c -> c.getName() != null && c.getName().equalsIgnoreCase(description));

                    if (!exists) {
                        createContractFromTransactions(owner, description, transactions);
                        createdContracts++;
                    }
                }
            }
        }
        return createdContracts;
    }

    @Transactional
    public ContractDocumentAnalysisResult analyzeDocument(MultipartFile file) {
        if (!aiEnabled) {
            throw new LocalizedException("error.ai.disabled");
        }
        if (file == null || file.isEmpty()) {
            throw new LocalizedException("error.contract.documentEmpty");
        }

        User owner = currentUserService.getCurrentUser();
        String mimeType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception ex) {
            throw new LocalizedException("error.contract.documentReadFailed");
        }

        String aiResponse;
        try {
            aiResponse = geminiClient.generateWithInlineData(
                    DOCUMENT_SYSTEM_PROMPT,
                    mimeType,
                    bytes,
                    "Extract recurring contracts from this document.");
        } catch (RuntimeException ex) {
            throw new LocalizedException("error.contract.aiAnalysisFailed");
        }

        List<Map<String, Object>> parsed = parseContractJson(aiResponse);
        int created = 0;
        for (Map<String, Object> item : parsed) {
            String name = stringValue(item.get("name"));
            if (name == null || name.isBlank()) {
                continue;
            }
            boolean exists = contractRepository.findAllByOwnerId(owner.getId()).stream()
                    .anyMatch(c -> c.getName().equalsIgnoreCase(name));
            if (exists) {
                continue;
            }
            Contract contract = new Contract();
            contract.setName(name);
            contract.setAmount(toBigDecimal(item.get("amount")));
            contract.setStartDate(parseDate(stringValue(item.get("startDate"))));
            contract.setEndDate(LocalDate.now().plusYears(1));
            contract.setContractInterval(parseInterval(stringValue(item.get("interval"))));
            contract.setAutoDetected(true);
            contract.setOwner(owner);
            contractRepository.save(contract);
            created++;
        }

        String summary = created > 0
                ? "Created " + created + " contract(s) from document."
                : "No new recurring contracts detected in document.";
        return new ContractDocumentAnalysisResult(created, summary);
    }

    private List<Map<String, Object>> parseContractJson(String aiResponse) {
        try {
            String json = aiResponse.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("^```(?:json)?\\s*", "").replaceAll("\\s*```$", "");
            }
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception ex) {
            return List.of();
        }
    }

    private void createContractFromTransactions(User owner, String name, List<PaymentTransaction> transactions) {
        PaymentTransaction latest = transactions.get(transactions.size() - 1);

        Contract contract = new Contract();
        contract.setName(name);
        contract.setAmount(latest.getAmount());
        contract.setStartDate(transactions.get(0).getTransactionDate());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setContractInterval(ContractInterval.MONTHLY);
        contract.setAutoDetected(true);
        contract.setOwner(owner);

        contractRepository.save(contract);
    }

    private static String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception ex) {
            return LocalDate.now();
        }
    }

    private static ContractInterval parseInterval(String value) {
        if (value == null) {
            return ContractInterval.MONTHLY;
        }
        try {
            return ContractInterval.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ContractInterval.MONTHLY;
        }
    }
}

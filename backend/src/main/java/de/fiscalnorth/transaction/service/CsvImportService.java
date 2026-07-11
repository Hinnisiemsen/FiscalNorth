package de.fiscalnorth.transaction.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.service.HouseholdScopeService;
import de.fiscalnorth.transaction.dto.CsvImportResult;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.model.TransactionType;
import de.fiscalnorth.shared.Messages;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.user.model.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    public enum BankPreset {
        SPARKASSE("Buchungstag", "Betrag", "Verwendungszweck", "Beguenstigter/Zahlungspflichtiger", ";",
                "dd.MM.yyyy", true),
        ING("Buchung", "Betrag", "Verwendungszweck", "Auftraggeber/Empfänger", ";",
                "dd.MM.yyyy", true);

        public final String dateColumn;
        public final String amountColumn;
        public final String purposeColumn;
        public final String partyColumn;
        public final String delimiter;
        public final String dateFormat;
        public final boolean germanNumberFormat; // 1.234,56

        BankPreset(String dateColumn, String amountColumn, String purposeColumn, String partyColumn,
                   String delimiter, String dateFormat, boolean germanNumberFormat) {
            this.dateColumn = dateColumn;
            this.amountColumn = amountColumn;
            this.purposeColumn = purposeColumn;
            this.partyColumn = partyColumn;
            this.delimiter = delimiter;
            this.dateFormat = dateFormat;
            this.germanNumberFormat = germanNumberFormat;
        }
    }

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CategoryRepository categoryRepository;
    private final Messages messages;
    private final CurrentUserService currentUserService;
    private final HouseholdScopeService householdScopeService;

    public CsvImportService(PaymentTransactionRepository paymentTransactionRepository,
                            CategoryRepository categoryRepository,
                            Messages messages,
                            CurrentUserService currentUserService,
                            HouseholdScopeService householdScopeService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.categoryRepository = categoryRepository;
        this.messages = messages;
        this.currentUserService = currentUserService;
        this.householdScopeService = householdScopeService;
    }

    @Transactional
    public CsvImportResult importFromCsv(MultipartFile file, BankPreset preset) {
        User owner = currentUserService.getCurrentUser();
        Household household = householdScopeService.requireHousehold();
        int imported = 0;
        int skippedDuplicates = 0;
        List<String> errors = new ArrayList<>();

        try {
            // Try common encodings: UTF-8 first, then Windows-1252 (common for German exports)
            String content = readFileContent(file);
            if (content == null || content.isBlank()) {
                return new CsvImportResult(0, 0, 1, List.of(messages.get("csv.fileEmpty")));
            }

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setDelimiter(preset.delimiter.charAt(0))
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            try (CSVParser parser = CSVParser.parse(new StringReader(content), format)) {
                List<CSVRecord> records = parser.getRecords();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(preset.dateFormat);

                for (CSVRecord record : records) {
                    try {
                        String dateStr = getValue(record, preset.dateColumn);
                        String amountStr = getValue(record, preset.amountColumn);
                        String purpose = getValue(record, preset.purposeColumn);
                        String party = getValue(record, preset.partyColumn);

                        if (dateStr == null || dateStr.isBlank() || amountStr == null || amountStr.isBlank()) {
                            continue; // Skip empty rows
                        }

                        LocalDate date = LocalDate.parse(dateStr.trim(), dateFormatter);
                        BigDecimal amount = parseAmount(amountStr, preset.germanNumberFormat);
                        String description = buildDescription(purpose, party);

                        String hash = computeImportHash(date, amount, description);
                        if (paymentTransactionRepository.existsByOwnerIdAndImportHash(owner.getId(), hash)) {
                            skippedDuplicates++;
                            continue;
                        }

                        PaymentTransaction tx = new PaymentTransaction();
                        tx.setTransactionDate(date);
                        tx.setAmount(amount.abs());
                        tx.setTransactionType(amount.compareTo(BigDecimal.ZERO) >= 0 ? TransactionType.Income : TransactionType.Expense);
                        tx.setDescription(description);
                        tx.setImportHash(hash);
                        tx.setCategory(categorizeTransaction(description, owner, household.getId()));
                        tx.setOwner(owner);
                        tx.setHousehold(household);
                        paymentTransactionRepository.save(tx);
                        imported++;
                    } catch (Exception e) {
                        errors.add(messages.get("csv.rowError", record.getRecordNumber() + 1, e.getMessage()));
                    }
                }
            }
        } catch (Exception e) {
            errors.add(messages.get("csv.parseError", e.getMessage()));
        }

        return new CsvImportResult(imported, skippedDuplicates, errors.size(), errors);
    }

    private String readFileContent(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            // Try UTF-8 first
            String utf8 = new String(bytes, StandardCharsets.UTF_8);
            if (utf8.contains("�")) {
                // Fallback to Windows-1252 for German bank exports
                return new String(bytes, "Windows-1252");
            }
            return utf8;
        } catch (Exception e) {
            try {
                return new String(file.getBytes(), "Windows-1252");
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private String getValue(CSVRecord record, String columnName) {
        try {
            String val = record.get(columnName);
            return val != null ? val : "";
        } catch (IllegalArgumentException e) {
            for (String header : record.getParser().getHeaderNames()) {
                if (header != null && header.trim().equalsIgnoreCase(columnName.trim())) {
                    return record.get(header);
                }
            }
            return "";
        }
    }

    private BigDecimal parseAmount(String amountStr, boolean germanFormat) {
        String cleaned = amountStr.trim()
                .replace("\"", "")
                .replace("'", "")
                .replace(" ", "")
                .replace("(", "-")
                .replace(")", "");
        if (germanFormat) {
            cleaned = cleaned.replace(".", "").replace(",", ".");
        }
        return new BigDecimal(cleaned);
    }

    private String buildDescription(String purpose, String party) {
        StringBuilder sb = new StringBuilder();
        if (party != null && !party.isBlank()) {
            sb.append(party.trim());
        }
        if (purpose != null && !purpose.isBlank()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(purpose.trim());
        }
        return sb.length() > 0 ? sb.toString() : "Imported transaction";
    }

    private String computeImportHash(LocalDate date, BigDecimal amount, String description) {
        String payload = date + "|" + amount + "|" + (description != null ? description : "");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(payload.hashCode()); // Fallback
        }
    }

    private Category categorizeTransaction(String description, User owner, Long householdId) {
        if (description == null) return findOrCreateCategory("General", TransactionType.Expense, owner, householdId);
        String lower = description.toLowerCase();
        if (lower.contains("rewe") || lower.contains("lidl") || lower.contains("aldi") || lower.contains("edeka")) {
            return findOrCreateCategory("Groceries", TransactionType.Expense, owner, householdId);
        }
        if (lower.contains("netflix") || lower.contains("spotify") || lower.contains("prime") || lower.contains("disney")) {
            return findOrCreateCategory("Entertainment", TransactionType.Expense, owner, householdId);
        }
        if (lower.contains("shell") || lower.contains("aral") || lower.contains("total") || lower.contains("esso")) {
            return findOrCreateCategory("Transport", TransactionType.Expense, owner, householdId);
        }
        if (lower.contains("miete") || lower.contains("rent") || lower.contains("wohnung")) {
            return findOrCreateCategory("Rent", TransactionType.Expense, owner, householdId);
        }
        if (lower.contains("gehalt") || lower.contains("salary") || lower.contains("lohn")) {
            return findOrCreateCategory("Salary", TransactionType.Income, owner, householdId);
        }
        return findOrCreateCategory("General", TransactionType.Expense, owner, householdId);
    }

    private Category findOrCreateCategory(String name, TransactionType type, User owner, Long householdId) {
        return categoryRepository.findByHouseholdIdAndNameAndTransactionType(householdId, name, type)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setTransactionType(type);
                    c.setOwner(owner);
                    c.setHousehold(householdScopeService.requireHousehold());
                    return categoryRepository.save(c);
                });
    }
}

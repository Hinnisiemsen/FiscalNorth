package de.fiscalnorth.transaction.dto;

import java.util.List;

public record CsvImportResult(
        int imported,
        int skippedDuplicates,
        int parseErrors,
        List<String> errorMessages) {
}

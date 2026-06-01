package de.fiscalnorth.transaction.controller;

import de.fiscalnorth.transaction.dto.CsvImportResult;
import de.fiscalnorth.transaction.service.CsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transaction/import")
public class CsvImportController {

    private final CsvImportService csvImportService;

    public CsvImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping("/csv")
    public ResponseEntity<CsvImportResult> importCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "preset", defaultValue = "SPARKASSE") CsvImportService.BankPreset preset) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CsvImportResult(0, 0, 1, java.util.List.of("File is empty")));
        }
        CsvImportResult result = csvImportService.importFromCsv(file, preset);
        return ResponseEntity.ok(result);
    }
}

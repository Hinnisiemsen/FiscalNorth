package de.fiscalnorth.transaction.controller;

import de.fiscalnorth.transaction.dto.CreateTransferTransactionRequest;
import de.fiscalnorth.transaction.model.TransferTransaction;
import de.fiscalnorth.transaction.service.TransferTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transaction/transfer")
public class TransferTransactionController {
    private final TransferTransactionService transferTransactionService;

    public TransferTransactionController(TransferTransactionService transferTransactionService) {
        this.transferTransactionService = transferTransactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransferTransaction>> getAllTransferTransactions() {
        return ResponseEntity.ok(transferTransactionService.getAllTransferTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferTransaction> getTransferTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transferTransactionService.getTransferTransactionById(id));
    }

    @PostMapping
    public ResponseEntity<TransferTransaction> createTransferTransaction(
            @RequestBody @Valid CreateTransferTransactionRequest request) {
        TransferTransaction transaction = transferTransactionService.createTransferTransaction(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransferTransaction(@PathVariable Long id) {
        transferTransactionService.deleteTransferTransaction(id);
        return ResponseEntity.noContent().build();
    }
}

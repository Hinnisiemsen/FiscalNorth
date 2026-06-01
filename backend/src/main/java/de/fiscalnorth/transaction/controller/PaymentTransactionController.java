package de.fiscalnorth.transaction.controller;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.service.CategoryService;
import de.fiscalnorth.transaction.dto.CreatePaymentTransactionRequest;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.service.PaymentTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transaction/payment")
public class PaymentTransactionController {
    private final PaymentTransactionService paymentTransactionService;
    private final CategoryService categoryService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService,
            CategoryService categoryService) {
        this.paymentTransactionService = paymentTransactionService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentTransaction>> getAllPaymentTransactions() {
        return ResponseEntity.ok(paymentTransactionService.getAllPaymentTransactions());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PaymentTransaction>> getRecentPaymentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        return ResponseEntity.ok(paymentTransactionService.getRecentTransactions(safeLimit));
    }

    @PostMapping
    public ResponseEntity<PaymentTransaction> createPaymentTransaction(
            CreatePaymentTransactionRequest paymentTransactionRequest) {
        PaymentTransaction paymentTransaction = paymentTransactionService
                .createPaymentTransaction(paymentTransactionRequest);
        return new ResponseEntity<>(paymentTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PaymentTransaction>> getPaymentTransactionsByCategory(@PathVariable Long categoryId) {
        Category category = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(paymentTransactionService.getExpensesBySingleCategory(category));
    }

    @GetMapping("/amount/{amount}")
    public ResponseEntity<List<PaymentTransaction>> getPaymentTransactionsOverAmount(@PathVariable BigDecimal amount) {
        return ResponseEntity.ok(paymentTransactionService.getExpensesOverValue(amount));
    }
}

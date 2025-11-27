package de.fiscalnorth.transaction.controller;

import de.fiscalnorth.transaction.dto.CreatePaymentTransactionRequest;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.service.PaymentTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction/payment")
public class PaymentTransactionController {
    private final PaymentTransactionService paymentTransactionService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    @PostMapping
    public ResponseEntity<PaymentTransaction> createPaymentTransaction(CreatePaymentTransactionRequest paymentTransactionRequest) {
        PaymentTransaction paymentTransaction = paymentTransactionService.createPaymentTransaction(paymentTransactionRequest);
        return new ResponseEntity<>(paymentTransaction, HttpStatus.CREATED);
    }
}

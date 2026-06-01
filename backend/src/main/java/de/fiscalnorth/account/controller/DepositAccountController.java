package de.fiscalnorth.account.controller;

import de.fiscalnorth.account.dto.CreateDepositAccountRequest;
import de.fiscalnorth.account.model.DepositAccount;
import de.fiscalnorth.account.service.DepositAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/account/deposit")
public class DepositAccountController {
    private final DepositAccountService depositAccountService;

    public DepositAccountController(DepositAccountService depositAccountService) {
        this.depositAccountService = depositAccountService;
    }

    @GetMapping
    public ResponseEntity<List<DepositAccount>> getAllDepositAccounts() {
        return ResponseEntity.ok(depositAccountService.getAllDepositAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepositAccount> getDepositAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(depositAccountService.getDepositAccountById(id));
    }

    @PostMapping
    public ResponseEntity<DepositAccount> createDepositAccount(
            @RequestBody @Valid CreateDepositAccountRequest request) {
        DepositAccount account = depositAccountService.createDepositAccount(request);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepositAccount(@PathVariable Long id) {
        depositAccountService.deleteDepositAccount(id);
        return ResponseEntity.noContent().build();
    }
}

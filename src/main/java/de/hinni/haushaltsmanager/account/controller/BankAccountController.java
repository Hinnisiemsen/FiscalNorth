package de.hinni.haushaltsmanager.account.controller;

import de.hinni.haushaltsmanager.account.dto.CreateBankAccountRequest;
import de.hinni.haushaltsmanager.account.model.BankAccount;
import de.hinni.haushaltsmanager.account.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/account/bank")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> getAllBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllBankAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getBankAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getBankAccountById(id));
    }

    @PostMapping
    public ResponseEntity<BankAccount> addNewBankAccount(@RequestBody @Valid CreateBankAccountRequest request) {
        BankAccount newBankAccount = bankAccountService.addNewBankAccount(request);
        return new ResponseEntity<>(newBankAccount, HttpStatus.CREATED);
    }
}

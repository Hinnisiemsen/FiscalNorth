package de.fiscalnorth.account.controller;

import de.fiscalnorth.account.dto.CreateCryptoAccountRequest;
import de.fiscalnorth.account.model.CryptoAccount;
import de.fiscalnorth.account.service.CryptoAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account/crypto")
@RequiredArgsConstructor
public class CryptoAccountController {

    private final CryptoAccountService cryptoAccountService;

    @GetMapping
    public ResponseEntity<List<CryptoAccount>> getAllCryptoAccounts() {
        return ResponseEntity.ok(cryptoAccountService.getAllCryptoAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CryptoAccount> getCryptoAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(cryptoAccountService.getCryptoAccountById(id));
    }

    @PostMapping
    public ResponseEntity<CryptoAccount> createCryptoAccount(@RequestBody @Valid CreateCryptoAccountRequest request) {
        CryptoAccount account = cryptoAccountService.createCryptoAccount(request);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }
}

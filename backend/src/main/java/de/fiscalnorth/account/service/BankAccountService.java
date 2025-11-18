package de.fiscalnorth.account.service;

import de.fiscalnorth.account.dto.CreateBankAccountRequest;
import de.fiscalnorth.account.model.BankAccount;
import de.fiscalnorth.account.repository.BankAccountRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public BankAccount getBankAccountById(Long bankAccountId) {
        return bankAccountRepository.getBankAccountsById(bankAccountId)
                .orElseThrow(() -> new RessourceNotFoundException("BankAccount", "id", bankAccountId));
    }

    @Transactional
    public BankAccount addNewBankAccount(CreateBankAccountRequest createBankAccountRequest) {
        BankAccount newBankAccount = new BankAccount();

        newBankAccount.setBankName(createBankAccountRequest.bankName());
        newBankAccount.setIban(createBankAccountRequest.iban());
        newBankAccount.setBic(createBankAccountRequest.bic());
        newBankAccount.setAccountType(createBankAccountRequest.accountType());

        if (bankAccountRepository.existsBankAccountsByIbanIsAndBicIs(newBankAccount.getIban(), newBankAccount.getBic())) {
            throw new IllegalStateException("Bank Account with this IBAN and BIC already exists!");
        }

        return bankAccountRepository.save(newBankAccount);
    }

}

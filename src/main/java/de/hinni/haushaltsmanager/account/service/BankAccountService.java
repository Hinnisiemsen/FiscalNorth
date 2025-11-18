package de.hinni.haushaltsmanager.account.service;

import de.hinni.haushaltsmanager.account.dto.CreateBankAccountRequest;
import de.hinni.haushaltsmanager.account.model.AccountType;
import de.hinni.haushaltsmanager.account.model.BankAccount;
import de.hinni.haushaltsmanager.account.repository.BankAccountRepository;
import de.hinni.haushaltsmanager.shared.RessourceNotFoundException;
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

package de.fiscalnorth.account.service;

import de.fiscalnorth.account.dto.CreateBankAccountRequest;
import de.fiscalnorth.account.model.BankAccount;
import de.fiscalnorth.account.repository.BankAccountRepository;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.shared.LocalizedException;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.shared.SupportedCurrency;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final CurrentUserService currentUserService;

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAllByOwnerId(currentUserService.getCurrentUserId());
    }

    public BankAccount getBankAccountById(Long bankAccountId) {
        return bankAccountRepository.findByIdAndOwnerId(bankAccountId, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("BankAccount", "id", bankAccountId));
    }

    @Transactional
    public BankAccount addNewBankAccount(CreateBankAccountRequest createBankAccountRequest) {
        User owner = currentUserService.getCurrentUser();
        BankAccount newBankAccount = new BankAccount();

        newBankAccount.setBankName(createBankAccountRequest.bankName());
        newBankAccount.setIban(createBankAccountRequest.iban());
        newBankAccount.setBic(createBankAccountRequest.bic());
        newBankAccount.setAccountType(createBankAccountRequest.accountType());
        newBankAccount.setName(
                createBankAccountRequest.name() != null && !createBankAccountRequest.name().isBlank()
                        ? createBankAccountRequest.name()
                        : createBankAccountRequest.bankName());
        newBankAccount.setCurrency(
                createBankAccountRequest.currency() != null
                        ? createBankAccountRequest.currency()
                        : SupportedCurrency.EURO);
        newBankAccount.setBalance(
                createBankAccountRequest.balance() != null
                        ? createBankAccountRequest.balance()
                        : BigDecimal.ZERO);
        newBankAccount.setOwner(owner);

        if (bankAccountRepository.existsByOwnerIdAndIbanIsAndBicIs(
                owner.getId(), newBankAccount.getIban(), newBankAccount.getBic())) {
            throw new LocalizedException("error.bankAccount.duplicate");
        }

        return bankAccountRepository.save(newBankAccount);
    }
}

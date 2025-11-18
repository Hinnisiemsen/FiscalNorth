package de.fiscalnorth.account.repository;

import de.fiscalnorth.account.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> getBankAccountsById(Long bankAccountId);

    boolean existsBankAccountsByIbanIsAndBicIs(String iban, String bic);
}

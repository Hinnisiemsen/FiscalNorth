package de.fiscalnorth.account.repository;

import de.fiscalnorth.account.model.DepositAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositAccountRepository extends JpaRepository<DepositAccount, Long> {

    java.util.Optional<DepositAccount> findByExternalId(String externalId);

    java.util.List<DepositAccount> findByBankConsentId(Long bankConsentId);
}

package de.fiscalnorth.account.repository;

import de.fiscalnorth.account.model.DepositAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositAccountRepository extends JpaRepository<DepositAccount, Long> {
}

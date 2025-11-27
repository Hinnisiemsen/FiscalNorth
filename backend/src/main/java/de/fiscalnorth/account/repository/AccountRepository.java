package de.fiscalnorth.account.repository;

import de.fiscalnorth.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}

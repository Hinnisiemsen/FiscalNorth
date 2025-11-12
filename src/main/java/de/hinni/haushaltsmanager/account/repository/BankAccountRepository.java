package de.hinni.haushaltsmanager.account.repository;

import de.hinni.haushaltsmanager.account.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}

package de.hinni.haushaltsmanager.account.repository;

import de.hinni.haushaltsmanager.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}

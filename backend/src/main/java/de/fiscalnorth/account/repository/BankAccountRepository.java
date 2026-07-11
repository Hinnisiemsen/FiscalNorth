package de.fiscalnorth.account.repository;

import de.fiscalnorth.account.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findAllByOwnerId(Long ownerId);

    List<BankAccount> findAllByHouseholdId(Long householdId);

    Optional<BankAccount> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<BankAccount> findByIdAndHouseholdId(Long id, Long householdId);

    boolean existsByOwnerIdAndIbanIsAndBicIs(Long ownerId, String iban, String bic);
}

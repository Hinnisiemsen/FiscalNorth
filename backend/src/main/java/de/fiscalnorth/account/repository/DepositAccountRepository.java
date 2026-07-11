package de.fiscalnorth.account.repository;

import de.fiscalnorth.account.model.DepositAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepositAccountRepository extends JpaRepository<DepositAccount, Long> {

    List<DepositAccount> findAllByOwnerId(Long ownerId);

    Optional<DepositAccount> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<DepositAccount> findByIdAndHouseholdId(Long id, Long householdId);

    Optional<DepositAccount> findByOwnerIdAndExternalId(Long ownerId, String externalId);

    List<DepositAccount> findAllByHouseholdId(Long householdId);

    List<DepositAccount> findByOwnerIdAndBankConsentId(Long ownerId, Long bankConsentId);
}

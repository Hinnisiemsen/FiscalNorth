package de.fiscalnorth.contract.repository;

import de.fiscalnorth.contract.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findAllByOwnerId(Long ownerId);

    List<Contract> findAllByHouseholdId(Long householdId);

    Optional<Contract> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Contract> findByIdAndHouseholdId(Long id, Long householdId);
}

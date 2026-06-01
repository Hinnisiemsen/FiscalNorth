package de.fiscalnorth.contract.repository;

import de.fiscalnorth.contract.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findAllByOwnerId(Long ownerId);

    Optional<Contract> findByIdAndOwnerId(Long id, Long ownerId);
}

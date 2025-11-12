package de.hinni.haushaltsmanager.contract.repository;

import de.hinni.haushaltsmanager.contract.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}

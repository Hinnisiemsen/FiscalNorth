package de.hinni.haushaltsmanager.transaction.repository;

import de.hinni.haushaltsmanager.transaction.model.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, Long> {
}

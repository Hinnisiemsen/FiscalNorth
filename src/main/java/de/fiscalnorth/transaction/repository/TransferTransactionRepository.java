package de.fiscalnorth.transaction.repository;

import de.fiscalnorth.transaction.model.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, Long> {
}

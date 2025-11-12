package de.hinni.haushaltsmanager.transaction.repository;

import de.hinni.haushaltsmanager.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

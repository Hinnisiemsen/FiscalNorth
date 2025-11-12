package de.hinni.haushaltsmanager.transaction.repository;

import de.hinni.haushaltsmanager.transaction.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
}

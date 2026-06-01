package de.fiscalnorth.transaction.repository;

import de.fiscalnorth.transaction.model.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, Long> {
    List<TransferTransaction> findAllByOwnerId(Long ownerId);

    Optional<TransferTransaction> findByIdAndOwnerId(Long id, Long ownerId);
}

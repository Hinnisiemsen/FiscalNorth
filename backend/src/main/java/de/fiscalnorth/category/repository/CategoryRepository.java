package de.fiscalnorth.category.repository;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.transaction.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByOwnerId(Long ownerId);

    boolean existsByOwnerIdAndNameEqualsAndTransactionTypeEquals(Long ownerId, String name, TransactionType transactionType);

    List<Category> findAllByOwnerId(Long ownerId);

    Optional<Category> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Category> findByOwnerIdAndNameAndTransactionType(Long ownerId, String name, TransactionType transactionType);
}

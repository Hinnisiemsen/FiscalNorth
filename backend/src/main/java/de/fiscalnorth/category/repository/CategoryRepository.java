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

    List<Category> findAllByHouseholdId(Long householdId);

    Optional<Category> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Category> findByIdAndHouseholdId(Long id, Long householdId);

    Optional<Category> findByOwnerIdAndNameAndTransactionType(Long ownerId, String name, TransactionType transactionType);

    Optional<Category> findByHouseholdIdAndNameAndTransactionType(Long householdId, String name, TransactionType transactionType);

    boolean existsByHouseholdIdAndNameEqualsAndTransactionTypeEquals(Long householdId, String name, TransactionType transactionType);
}

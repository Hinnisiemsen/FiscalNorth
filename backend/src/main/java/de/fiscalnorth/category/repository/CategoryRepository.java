package de.fiscalnorth.category.repository;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.transaction.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameEqualsAndTransactionTypeEquals(String name, TransactionType transactionType);

    List<Category> findCategoryByNameEquals(String name);
}

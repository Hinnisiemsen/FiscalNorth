package de.fiscalnorth.transaction.repository;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    boolean existsByOwnerIdAndImportHash(Long ownerId, String importHash);

    List<PaymentTransaction> findAllByOwnerId(Long ownerId);

    Optional<PaymentTransaction> findByIdAndOwnerId(Long id, Long ownerId);

    List<PaymentTransaction> findAllByOwnerIdOrderByTransactionDateDesc(Long ownerId, Pageable pageable);

    List<PaymentTransaction> findByOwnerIdAndCategory(Long ownerId, Category category);

    List<PaymentTransaction> findByOwnerIdAndAmountGreaterThan(Long ownerId, BigDecimal amountIsGreaterThan);

    @Query("select pt from PaymentTransaction pt where pt.owner.id = :ownerId and pt.tags like %:tag%")
    List<PaymentTransaction> findByOwnerIdAndTagsContaining(@Param("ownerId") Long ownerId, @Param("tag") String tag);

    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.owner.id = :ownerId AND pt.category.name = :categoryName AND pt.transactionType = 'Expense' AND pt.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpenseAmountByCategoryNameAndDateRange(@Param("ownerId") Long ownerId, @Param("categoryName") String categoryName, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.owner.id = :ownerId AND pt.category.id = :categoryId AND pt.transactionType = 'Expense' AND pt.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpenseAmountByCategoryIdAndDateRange(@Param("ownerId") Long ownerId, @Param("categoryId") Long categoryId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.owner.id = :ownerId AND pt.category.id = :categoryId AND pt.transactionType = 'Expense' AND pt.transactionDate BETWEEN :startDate AND :endDate AND NOT EXISTS (SELECT 1 FROM TransactionSplit ts WHERE ts.payment = pt)")
    BigDecimal sumExpenseAmountByCategoryIdAndDateRangeExcludingSplitParents(@Param("ownerId") Long ownerId, @Param("categoryId") Long categoryId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT pt.category.name, SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.owner.id = :ownerId AND pt.transactionType = de.fiscalnorth.transaction.model.TransactionType.Expense AND pt.transactionDate BETWEEN :startDate AND :endDate AND pt.category IS NOT NULL AND NOT EXISTS (SELECT 1 FROM TransactionSplit ts WHERE ts.payment = pt) GROUP BY pt.category.name")
    List<Object[]> sumExpensesByCategoryBetweenExcludingSplitParents(@Param("ownerId") Long ownerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT EXTRACT(YEAR FROM transaction_date), EXTRACT(MONTH FROM transaction_date), transaction_type, SUM(amount) FROM TRANSACTION WHERE dtype = 'PaymentTransaction' AND owner_id = :ownerId AND transaction_date BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM transaction_date), EXTRACT(MONTH FROM transaction_date), transaction_type", nativeQuery = true)
    List<Object[]> sumByMonthAndTypeBetween(@Param("ownerId") Long ownerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

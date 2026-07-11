package de.fiscalnorth.transaction.repository;

import de.fiscalnorth.transaction.model.TransactionSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionSplitRepository extends JpaRepository<TransactionSplit, Long> {

    List<TransactionSplit> findAllByPaymentIdAndPaymentOwnerId(Long paymentId, Long ownerId);

    @Query("""
            SELECT COALESCE(SUM(ts.amount), 0) FROM TransactionSplit ts
            JOIN ts.payment p
            WHERE p.owner.id = :ownerId
              AND ts.category.id = :categoryId
              AND p.transactionType = de.fiscalnorth.transaction.model.TransactionType.Expense
              AND p.transactionDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumExpenseAmountByCategoryIdAndDateRange(
            @Param("ownerId") Long ownerId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT ts.category.name, SUM(ts.amount) FROM TransactionSplit ts
            JOIN ts.payment p
            WHERE p.owner.id = :ownerId
              AND p.transactionType = de.fiscalnorth.transaction.model.TransactionType.Expense
              AND p.transactionDate BETWEEN :startDate AND :endDate
              AND ts.category IS NOT NULL
            GROUP BY ts.category.name
            """)
    List<Object[]> sumExpensesByCategoryBetween(
            @Param("ownerId") Long ownerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

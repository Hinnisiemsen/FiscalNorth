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

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    boolean existsByImportHash(String importHash);

    List<PaymentTransaction> findAllByOrderByTransactionDateDesc(Pageable pageable);

    List<PaymentTransaction> findPaymentTransactionByCategory(Category category);

    List<PaymentTransaction> findPaymentTransactionByAmountGreaterThan(BigDecimal amountIsGreaterThan);

    @Query("select pt from PaymentTransaction pt where pt.tags like %:tag%")
    List<PaymentTransaction> findPaymentTransactionByTagsIs(@Param("tag") String tag);

    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.category.name = :categoryName AND pt.transactionType = 'Expense' AND pt.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpenseAmountByCategoryNameAndDateRange(@Param("categoryName") String categoryName, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.category.id = :categoryId AND pt.transactionType = 'Expense' AND pt.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpenseAmountByCategoryIdAndDateRange(@Param("categoryId") Long categoryId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT pt.category.name, SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.transactionType = de.fiscalnorth.transaction.model.TransactionType.Expense AND pt.transactionDate BETWEEN :startDate AND :endDate AND pt.category IS NOT NULL GROUP BY pt.category.name")
    List<Object[]> sumExpensesByCategoryBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT EXTRACT(YEAR FROM transaction_date), EXTRACT(MONTH FROM transaction_date), transaction_type, SUM(amount) FROM TRANSACTION WHERE dtype = 'PaymentTransaction' AND transaction_date BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM transaction_date), EXTRACT(MONTH FROM transaction_date), transaction_type", nativeQuery = true)
    List<Object[]> sumByMonthAndTypeBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

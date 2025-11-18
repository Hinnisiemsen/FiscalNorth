package de.hinni.haushaltsmanager.transaction.repository;

import de.hinni.haushaltsmanager.category.model.Category;
import de.hinni.haushaltsmanager.transaction.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findPaymentTransactionByCategory(Category category);

    List<PaymentTransaction> findPaymentTransactionByAmountGreaterThan(BigDecimal amountIsGreaterThan);

    @Query("select pt from PaymentTransaction pt where pt.tags like %:tag%")
    List<PaymentTransaction> findPaymentTransactionByTagsIs(@Param("tag") String tag);


}

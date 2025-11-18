package de.fiscalnorth.transaction.service;

import de.fiscalnorth.account.repository.BankAccountRepository;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentTransactionService {
    private PaymentTransactionRepository paymentTransactionRepository;
    private BankAccountRepository bankAccountRepository;
    private DepositAccountRepository depositAccountRepository;

    @Autowired
    public PaymentTransactionService(PaymentTransactionRepository paymentTransactionRepository,
                                     BankAccountRepository bankAccountRepository,
                                     DepositAccountRepository depositAccountRepository)
    {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.depositAccountRepository = depositAccountRepository;
    }



    @Transactional
    public List<PaymentTransaction> getExpensesBySingleCategory(Category category) {
        return paymentTransactionRepository.findPaymentTransactionByCategory(category);
    }

    @Transactional
    public List<List<PaymentTransaction>> getExpensesSeperatedIntoCategories(List<Category> categories) {
        List<List<PaymentTransaction>> resultList = new ArrayList<>(List.of());

        for (Category category: categories) {
            resultList.add(paymentTransactionRepository.findPaymentTransactionByCategory(category));
        }
        return resultList;
    }

    @Transactional
    public List<PaymentTransaction> getExpensesOverValue(BigDecimal value) {
        return paymentTransactionRepository.findPaymentTransactionByAmountGreaterThan(value);
    }
}

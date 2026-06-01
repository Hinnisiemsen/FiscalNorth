package de.fiscalnorth.transaction.service;

import de.fiscalnorth.account.repository.BankAccountRepository;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.transaction.dto.CreatePaymentTransactionRequest;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentTransactionService {
    private PaymentTransactionRepository paymentTransactionRepository;
    private BankAccountRepository bankAccountRepository;
    private DepositAccountRepository depositAccountRepository;
    private CategoryRepository categoryRepository;
    private ContractRepository contractRepository;

    @Autowired
    public PaymentTransactionService(PaymentTransactionRepository paymentTransactionRepository,
            BankAccountRepository bankAccountRepository,
            DepositAccountRepository depositAccountRepository,
            CategoryRepository categoryRepository,
            ContractRepository contractRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.categoryRepository = categoryRepository;
        this.contractRepository = contractRepository;
    }

    public List<PaymentTransaction> getAllPaymentTransactions() {
        return paymentTransactionRepository.findAll();
    }

    public List<PaymentTransaction> getRecentTransactions(int limit) {
        return paymentTransactionRepository.findAllByOrderByTransactionDateDesc(PageRequest.of(0, limit));
    }

    public List<PaymentTransaction> getExpensesBySingleCategory(Category category) {
        return paymentTransactionRepository.findPaymentTransactionByCategory(category);
    }

    public List<List<PaymentTransaction>> getExpensesSeperatedIntoCategories(List<Category> categories) {
        List<List<PaymentTransaction>> resultList = new ArrayList<>(List.of());

        for (Category category : categories) {
            resultList.add(paymentTransactionRepository.findPaymentTransactionByCategory(category));
        }
        return resultList;
    }

    public List<PaymentTransaction> getExpensesOverValue(BigDecimal value) {
        return paymentTransactionRepository.findPaymentTransactionByAmountGreaterThan(value);
    }

    @Transactional
    public PaymentTransaction createPaymentTransaction(CreatePaymentTransactionRequest paymentTransactionRequest) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();

        Category category = paymentTransactionRequest.category();
        if (category == null) {
            category = categorizeTransaction(paymentTransactionRequest.description());
        }

        checkCategoryAndContract(category, paymentTransactionRequest.contract());

        paymentTransaction.setTransactionDate(paymentTransactionRequest.transactionDate());
        paymentTransaction.setTransactionType(paymentTransactionRequest.transactionType());
        paymentTransaction.setAmount(paymentTransactionRequest.amount());
        paymentTransaction.setCategory(category);
        paymentTransaction.setContract(paymentTransactionRequest.contract());
        paymentTransaction.setDescription(paymentTransactionRequest.description());
        paymentTransaction.setTags(paymentTransactionRequest.tags());

        return paymentTransactionRepository.save(paymentTransaction);
    }

    private Category categorizeTransaction(String description) {
        if (description == null)
            return null;
        String descLower = description.toLowerCase();

        // Simple keyword mapping for MVP
        if (descLower.contains("rewe") || descLower.contains("lidl") || descLower.contains("aldi")) {
            return findOrCreateCategory("Groceries", de.fiscalnorth.transaction.model.TransactionType.Expense);
        } else if (descLower.contains("netflix") || descLower.contains("spotify") || descLower.contains("cinema")) {
            return findOrCreateCategory("Entertainment", de.fiscalnorth.transaction.model.TransactionType.Expense);
        } else if (descLower.contains("shell") || descLower.contains("aral")) {
            return findOrCreateCategory("Transport", de.fiscalnorth.transaction.model.TransactionType.Expense);
        } else if (descLower.contains("miete") || descLower.contains("rent")) {
            return findOrCreateCategory("Housing", de.fiscalnorth.transaction.model.TransactionType.Expense);
        } else if (descLower.contains("salary") || descLower.contains("gehalt")) {
            return findOrCreateCategory("Income", de.fiscalnorth.transaction.model.TransactionType.Income);
        }

        return findOrCreateCategory("General", de.fiscalnorth.transaction.model.TransactionType.Expense);
    }

    private Category findOrCreateCategory(String name) {
        return findOrCreateCategory(name, de.fiscalnorth.transaction.model.TransactionType.Expense);
    }

    private Category findOrCreateCategory(String name, de.fiscalnorth.transaction.model.TransactionType type) {
        return categoryRepository.findByNameAndTransactionType(name, type)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    category.setTransactionType(type);
                    return categoryRepository.save(category);
                });
    }

    private void checkCategoryAndContract(Category category, de.fiscalnorth.contract.model.Contract contract) {
        if (category != null && category.getId() != null) {
            if (!categoryRepository.existsById(category.getId())) {
                categoryRepository.save(category);
            }
        }
        if (contract != null && contract.getId() != null) {
            if (!contractRepository.existsById(contract.getId())) {
                contractRepository.save(contract);
            }
        }
    }
}

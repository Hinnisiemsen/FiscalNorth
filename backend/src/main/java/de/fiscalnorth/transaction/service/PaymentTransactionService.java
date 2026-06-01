package de.fiscalnorth.transaction.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.transaction.dto.CreatePaymentTransactionRequest;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.user.model.User;
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
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CategoryRepository categoryRepository;
    private final ContractRepository contractRepository;
    private final CurrentUserService currentUserService;

    @Autowired
    public PaymentTransactionService(PaymentTransactionRepository paymentTransactionRepository,
            CategoryRepository categoryRepository,
            ContractRepository contractRepository,
            CurrentUserService currentUserService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.categoryRepository = categoryRepository;
        this.contractRepository = contractRepository;
        this.currentUserService = currentUserService;
    }

    public List<PaymentTransaction> getAllPaymentTransactions() {
        return paymentTransactionRepository.findAllByOwnerId(currentUserService.getCurrentUserId());
    }

    public List<PaymentTransaction> getRecentTransactions(int limit) {
        return paymentTransactionRepository.findAllByOwnerIdOrderByTransactionDateDesc(
                currentUserService.getCurrentUserId(), PageRequest.of(0, limit));
    }

    public List<PaymentTransaction> getExpensesBySingleCategory(Category category) {
        return paymentTransactionRepository.findByOwnerIdAndCategory(currentUserService.getCurrentUserId(), category);
    }

    public List<List<PaymentTransaction>> getExpensesSeperatedIntoCategories(List<Category> categories) {
        Long ownerId = currentUserService.getCurrentUserId();
        List<List<PaymentTransaction>> resultList = new ArrayList<>(List.of());

        for (Category category : categories) {
            resultList.add(paymentTransactionRepository.findByOwnerIdAndCategory(ownerId, category));
        }
        return resultList;
    }

    public List<PaymentTransaction> getExpensesOverValue(BigDecimal value) {
        return paymentTransactionRepository.findByOwnerIdAndAmountGreaterThan(
                currentUserService.getCurrentUserId(), value);
    }

    @Transactional
    public PaymentTransaction createPaymentTransaction(CreatePaymentTransactionRequest paymentTransactionRequest) {
        User owner = currentUserService.getCurrentUser();
        PaymentTransaction paymentTransaction = new PaymentTransaction();

        Category category = paymentTransactionRequest.category();
        if (category == null) {
            category = categorizeTransaction(paymentTransactionRequest.description(), owner);
        }

        checkCategoryAndContract(category, paymentTransactionRequest.contract(), owner);

        paymentTransaction.setTransactionDate(paymentTransactionRequest.transactionDate());
        paymentTransaction.setTransactionType(paymentTransactionRequest.transactionType());
        paymentTransaction.setAmount(paymentTransactionRequest.amount());
        paymentTransaction.setCategory(category);
        paymentTransaction.setContract(paymentTransactionRequest.contract());
        paymentTransaction.setDescription(paymentTransactionRequest.description());
        paymentTransaction.setTags(paymentTransactionRequest.tags());
        paymentTransaction.setOwner(owner);

        return paymentTransactionRepository.save(paymentTransaction);
    }

    private Category categorizeTransaction(String description, User owner) {
        if (description == null)
            return null;
        String descLower = description.toLowerCase();

        if (descLower.contains("rewe") || descLower.contains("lidl") || descLower.contains("aldi")) {
            return findOrCreateCategory("Groceries", de.fiscalnorth.transaction.model.TransactionType.Expense, owner);
        } else if (descLower.contains("netflix") || descLower.contains("spotify") || descLower.contains("cinema")) {
            return findOrCreateCategory("Entertainment", de.fiscalnorth.transaction.model.TransactionType.Expense, owner);
        } else if (descLower.contains("shell") || descLower.contains("aral")) {
            return findOrCreateCategory("Transport", de.fiscalnorth.transaction.model.TransactionType.Expense, owner);
        } else if (descLower.contains("miete") || descLower.contains("rent")) {
            return findOrCreateCategory("Housing", de.fiscalnorth.transaction.model.TransactionType.Expense, owner);
        } else if (descLower.contains("salary") || descLower.contains("gehalt")) {
            return findOrCreateCategory("Income", de.fiscalnorth.transaction.model.TransactionType.Income, owner);
        }

        return findOrCreateCategory("General", de.fiscalnorth.transaction.model.TransactionType.Expense, owner);
    }

    private Category findOrCreateCategory(String name, de.fiscalnorth.transaction.model.TransactionType type, User owner) {
        return categoryRepository.findByOwnerIdAndNameAndTransactionType(owner.getId(), name, type)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    category.setTransactionType(type);
                    category.setOwner(owner);
                    return categoryRepository.save(category);
                });
    }

    private void checkCategoryAndContract(Category category, de.fiscalnorth.contract.model.Contract contract, User owner) {
        if (category != null && category.getId() != null) {
            if (!categoryRepository.findByIdAndOwnerId(category.getId(), owner.getId()).isPresent()) {
                category.setOwner(owner);
                categoryRepository.save(category);
            }
        }
        if (contract != null && contract.getId() != null) {
            if (!contractRepository.findByIdAndOwnerId(contract.getId(), owner.getId()).isPresent()) {
                contract.setOwner(owner);
                contractRepository.save(contract);
            }
        }
    }
}

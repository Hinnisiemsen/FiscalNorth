package de.fiscalnorth.transaction.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.service.HouseholdScopeService;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.shared.LocalizedException;
import de.fiscalnorth.transaction.dto.CreatePaymentTransactionRequest;
import de.fiscalnorth.transaction.dto.SplitLineRequest;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.model.TransactionSplit;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.transaction.repository.TransactionSplitRepository;
import de.fiscalnorth.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TransactionSplitRepository transactionSplitRepository;
    private final CategoryRepository categoryRepository;
    private final ContractRepository contractRepository;
    private final CurrentUserService currentUserService;
    private final HouseholdScopeService householdScopeService;

    @Autowired
    public PaymentTransactionService(
            PaymentTransactionRepository paymentTransactionRepository,
            TransactionSplitRepository transactionSplitRepository,
            CategoryRepository categoryRepository,
            ContractRepository contractRepository,
            CurrentUserService currentUserService,
            HouseholdScopeService householdScopeService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.transactionSplitRepository = transactionSplitRepository;
        this.categoryRepository = categoryRepository;
        this.contractRepository = contractRepository;
        this.currentUserService = currentUserService;
        this.householdScopeService = householdScopeService;
    }

    public List<PaymentTransaction> getAllPaymentTransactions() {
        return paymentTransactionRepository.findAllByHouseholdId(householdScopeService.requireHouseholdId());
    }

    public PaymentTransaction getPaymentTransactionById(Long id) {
        return paymentTransactionRepository
                .findByIdAndHouseholdId(id, householdScopeService.requireHouseholdId())
                .orElseThrow(() -> new LocalizedException("error.transaction.notFound"));
    }

    public List<PaymentTransaction> getRecentTransactions(int limit) {
        return paymentTransactionRepository.findAllByHouseholdIdOrderByTransactionDateDesc(
                householdScopeService.requireHouseholdId(), PageRequest.of(0, limit));
    }

    public List<PaymentTransaction> getExpensesBySingleCategory(Category category) {
        return paymentTransactionRepository.findByOwnerIdAndCategory(currentUserService.getCurrentUserId(), category);
    }

    public List<List<PaymentTransaction>> getExpensesSeperatedIntoCategories(List<Category> categories) {
        Long householdId = householdScopeService.requireHouseholdId();
        List<List<PaymentTransaction>> resultList = new ArrayList<>(List.of());
        List<PaymentTransaction> all = paymentTransactionRepository.findAllByHouseholdId(householdId);
        for (Category category : categories) {
            resultList.add(all.stream().filter(tx -> category.equals(tx.getCategory())).toList());
        }
        return resultList;
    }

    public List<PaymentTransaction> getExpensesOverValue(BigDecimal value) {
        return paymentTransactionRepository.findAllByHouseholdId(householdScopeService.requireHouseholdId()).stream()
                .filter(tx -> tx.getAmount().compareTo(value) > 0)
                .toList();
    }

    public List<TransactionSplit> getSplits(Long paymentId) {
        return transactionSplitRepository.findAllByPaymentIdAndPaymentHouseholdId(
                paymentId, householdScopeService.requireHouseholdId());
    }

    @Transactional
    public PaymentTransaction createPaymentTransaction(CreatePaymentTransactionRequest paymentTransactionRequest) {
        User owner = currentUserService.getCurrentUser();
        Household household = householdScopeService.requireHousehold();
        PaymentTransaction paymentTransaction = new PaymentTransaction();

        Category category = paymentTransactionRequest.category();
        if (category == null && !hasSplits(paymentTransactionRequest.splits())) {
            category = categorizeTransaction(paymentTransactionRequest.description(), owner, household.getId());
        }

        checkCategoryAndContract(category, paymentTransactionRequest.contract(), owner, household.getId());

        paymentTransaction.setTransactionDate(paymentTransactionRequest.transactionDate());
        paymentTransaction.setTransactionType(paymentTransactionRequest.transactionType());
        paymentTransaction.setAmount(paymentTransactionRequest.amount());
        paymentTransaction.setCategory(category);
        paymentTransaction.setContract(paymentTransactionRequest.contract());
        paymentTransaction.setDescription(paymentTransactionRequest.description());
        paymentTransaction.setTags(paymentTransactionRequest.tags());
        paymentTransaction.setOwner(owner);
        paymentTransaction.setHousehold(household);

        if (hasSplits(paymentTransactionRequest.splits())) {
            paymentTransaction.setCategory(null);
            applySplits(paymentTransaction, paymentTransactionRequest.splits(), household.getId());
        }

        return paymentTransactionRepository.save(paymentTransaction);
    }

    @Transactional
    public List<TransactionSplit> replaceSplits(Long paymentId, List<SplitLineRequest> splitLines) {
        PaymentTransaction payment = getPaymentTransactionById(paymentId);
        payment.getSplits().clear();
        if (splitLines == null || splitLines.isEmpty()) {
            return List.of();
        }
        applySplits(payment, splitLines, householdScopeService.requireHouseholdId());
        payment.setCategory(null);
        return paymentTransactionRepository.save(payment).getSplits();
    }

    private void applySplits(PaymentTransaction payment, List<SplitLineRequest> splitLines, Long householdId) {
        BigDecimal total = BigDecimal.ZERO;
        for (SplitLineRequest line : splitLines) {
            Category splitCategory = categoryRepository
                    .findByIdAndHouseholdId(line.categoryId(), householdId)
                    .orElseThrow(() -> new LocalizedException("error.category.notFound"));
            TransactionSplit split = new TransactionSplit();
            split.setPayment(payment);
            split.setAmount(line.amount());
            split.setCategory(splitCategory);
            split.setNote(line.note());
            payment.getSplits().add(split);
            total = total.add(line.amount());
        }
        if (total.compareTo(payment.getAmount()) != 0) {
            throw new LocalizedException("error.transaction.splitSumMismatch");
        }
    }

    private boolean hasSplits(List<SplitLineRequest> splits) {
        return splits != null && !splits.isEmpty();
    }

    private Category categorizeTransaction(String description, User owner, Long householdId) {
        if (description == null)
            return null;
        String descLower = description.toLowerCase();

        if (descLower.contains("rewe") || descLower.contains("lidl") || descLower.contains("aldi")) {
            return findOrCreateCategory("Groceries", de.fiscalnorth.transaction.model.TransactionType.Expense, owner, householdId);
        } else if (descLower.contains("netflix") || descLower.contains("spotify") || descLower.contains("cinema")) {
            return findOrCreateCategory("Entertainment", de.fiscalnorth.transaction.model.TransactionType.Expense, owner, householdId);
        } else if (descLower.contains("shell") || descLower.contains("aral")) {
            return findOrCreateCategory("Transport", de.fiscalnorth.transaction.model.TransactionType.Expense, owner, householdId);
        } else if (descLower.contains("miete") || descLower.contains("rent")) {
            return findOrCreateCategory("Housing", de.fiscalnorth.transaction.model.TransactionType.Expense, owner, householdId);
        } else if (descLower.contains("salary") || descLower.contains("gehalt")) {
            return findOrCreateCategory("Income", de.fiscalnorth.transaction.model.TransactionType.Income, owner, householdId);
        }

        return findOrCreateCategory("General", de.fiscalnorth.transaction.model.TransactionType.Expense, owner, householdId);
    }

    private Category findOrCreateCategory(String name, de.fiscalnorth.transaction.model.TransactionType type, User owner, Long householdId) {
        return categoryRepository.findByHouseholdIdAndNameAndTransactionType(householdId, name, type)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    category.setTransactionType(type);
                    category.setOwner(owner);
                    category.setHousehold(householdScopeService.requireHousehold());
                    return categoryRepository.save(category);
                });
    }

    private void checkCategoryAndContract(Category category, de.fiscalnorth.contract.model.Contract contract, User owner, Long householdId) {
        if (category != null && category.getId() != null) {
            if (!categoryRepository.findByIdAndHouseholdId(category.getId(), householdId).isPresent()) {
                category.setOwner(owner);
                category.setHousehold(householdScopeService.requireHousehold());
                categoryRepository.save(category);
            }
        }
        if (contract != null && contract.getId() != null) {
            if (!contractRepository.findByIdAndHouseholdId(contract.getId(), householdId).isPresent()) {
                contract.setOwner(owner);
                contract.setHousehold(householdScopeService.requireHousehold());
                contractRepository.save(contract);
            }
        }
    }
}

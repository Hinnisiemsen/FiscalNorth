package de.fiscalnorth.transaction.service;

import de.fiscalnorth.account.repository.BankAccountRepository;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.transaction.dto.CreatePaymentTransactionRequest;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
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
                                     ContractRepository contractRepository)
    {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.categoryRepository = categoryRepository;
        this.contractRepository = contractRepository;
    }


    public List<PaymentTransaction> getExpensesBySingleCategory(Category category) {
        return paymentTransactionRepository.findPaymentTransactionByCategory(category);
    }

    public List<List<PaymentTransaction>> getExpensesSeperatedIntoCategories(List<Category> categories) {
        List<List<PaymentTransaction>> resultList = new ArrayList<>(List.of());

        for (Category category: categories) {
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

        checkCategoryAndContract(paymentTransactionRequest);

        paymentTransaction.setTransactionDate(paymentTransactionRequest.transactionDate());
        paymentTransaction.setTransactionType(paymentTransactionRequest.transactionType());
        paymentTransaction.setAmount(paymentTransactionRequest.amount());
        paymentTransaction.setCategory(paymentTransactionRequest.category());
        paymentTransaction.setContract(paymentTransactionRequest.contract());
        paymentTransaction.setDescription(paymentTransactionRequest.description());
        paymentTransaction.setTags(paymentTransactionRequest.tags());

        return paymentTransactionRepository.save(paymentTransaction);
    }

    private void checkCategoryAndContract(CreatePaymentTransactionRequest paymentTransactionRequest) {
        if (!categoryRepository.existsById(paymentTransactionRequest.category().getId())) {
            categoryRepository.save(paymentTransactionRequest.category());
        }
        if (!contractRepository.existsById(paymentTransactionRequest.contract().getId())) {
            contractRepository.save(paymentTransactionRequest.contract());
        }
    }
}

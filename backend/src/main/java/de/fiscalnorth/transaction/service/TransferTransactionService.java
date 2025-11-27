package de.fiscalnorth.transaction.service;

import de.fiscalnorth.account.model.Account;
import de.fiscalnorth.account.repository.AccountRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.transaction.dto.CreateTransferTransactionRequest;
import de.fiscalnorth.transaction.model.TransferTransaction;
import de.fiscalnorth.transaction.repository.TransferTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferTransactionService {
    private final TransferTransactionRepository transferTransactionRepository;
    private final AccountRepository accountRepository;

    public List<TransferTransaction> getAllTransferTransactions() {
        return transferTransactionRepository.findAll();
    }

    public TransferTransaction getTransferTransactionById(Long id) {
        return transferTransactionRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("TransferTransaction", "id", id));
    }

    @Transactional
    public TransferTransaction createTransferTransaction(CreateTransferTransactionRequest request) {
        Account fromAccount = accountRepository.findById(request.fromAccountId())
                .orElseThrow(() -> new RessourceNotFoundException("Account", "id", request.fromAccountId()));
        Account toAccount = accountRepository.findById(request.toAccountId())
                .orElseThrow(() -> new RessourceNotFoundException("Account", "id", request.toAccountId()));

        TransferTransaction transaction = new TransferTransaction();
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setTransactionType(request.transactionType());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);

        return transferTransactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransferTransaction(Long id) {
        if (!transferTransactionRepository.existsById(id)) {
            throw new RessourceNotFoundException("TransferTransaction", "id", id);
        }
        transferTransactionRepository.deleteById(id);
    }
}

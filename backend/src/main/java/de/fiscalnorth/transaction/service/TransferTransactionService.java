package de.fiscalnorth.transaction.service;

import de.fiscalnorth.account.model.Account;
import de.fiscalnorth.account.repository.AccountRepository;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.transaction.dto.CreateTransferTransactionRequest;
import de.fiscalnorth.transaction.model.TransferTransaction;
import de.fiscalnorth.transaction.repository.TransferTransactionRepository;
import de.fiscalnorth.user.model.User;
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
    private final CurrentUserService currentUserService;

    public List<TransferTransaction> getAllTransferTransactions() {
        return transferTransactionRepository.findAllByOwnerId(currentUserService.getCurrentUserId());
    }

    public TransferTransaction getTransferTransactionById(Long id) {
        return transferTransactionRepository.findByIdAndOwnerId(id, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("TransferTransaction", "id", id));
    }

    @Transactional
    public TransferTransaction createTransferTransaction(CreateTransferTransactionRequest request) {
        Long ownerId = currentUserService.getCurrentUserId();
        Account fromAccount = accountRepository.findByIdAndOwnerId(request.fromAccountId(), ownerId)
                .orElseThrow(() -> new RessourceNotFoundException("Account", "id", request.fromAccountId()));
        Account toAccount = accountRepository.findByIdAndOwnerId(request.toAccountId(), ownerId)
                .orElseThrow(() -> new RessourceNotFoundException("Account", "id", request.toAccountId()));

        User owner = currentUserService.getCurrentUser();
        TransferTransaction transaction = new TransferTransaction();
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setTransactionType(request.transactionType());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setOwner(owner);

        return transferTransactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransferTransaction(Long id) {
        TransferTransaction transaction = transferTransactionRepository
                .findByIdAndOwnerId(id, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("TransferTransaction", "id", id));
        transferTransactionRepository.delete(transaction);
    }
}

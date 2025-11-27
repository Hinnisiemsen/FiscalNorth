package de.fiscalnorth.account.service;

import de.fiscalnorth.account.dto.CreateDepositAccountRequest;
import de.fiscalnorth.account.model.DepositAccount;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositAccountService {
    private final DepositAccountRepository depositAccountRepository;

    public List<DepositAccount> getAllDepositAccounts() {
        return depositAccountRepository.findAll();
    }

    public DepositAccount getDepositAccountById(Long id) {
        return depositAccountRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("DepositAccount", "id", id));
    }

    @Transactional
    public DepositAccount createDepositAccount(CreateDepositAccountRequest request) {
        DepositAccount account = new DepositAccount();
        account.setName(request.name());
        account.setCurrency(request.currency());
        account.setBalance(request.balance());
        account.setInterestRate(request.interestRate());
        account.setTerm(request.term());
        account.setRenewable(request.renewable());
        return depositAccountRepository.save(account);
    }

    @Transactional
    public void deleteDepositAccount(Long id) {
        if (!depositAccountRepository.existsById(id)) {
            throw new RessourceNotFoundException("DepositAccount", "id", id);
        }
        depositAccountRepository.deleteById(id);
    }
}

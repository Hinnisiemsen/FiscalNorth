package de.fiscalnorth.account.service;

import de.fiscalnorth.account.dto.CreateDepositAccountRequest;
import de.fiscalnorth.account.model.DepositAccount;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.service.HouseholdScopeService;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositAccountService {
    private final DepositAccountRepository depositAccountRepository;
    private final CurrentUserService currentUserService;
    private final HouseholdScopeService householdScopeService;

    public List<DepositAccount> getAllDepositAccounts() {
        return depositAccountRepository.findAllByHouseholdId(householdScopeService.requireHouseholdId());
    }

    public DepositAccount getDepositAccountById(Long id) {
        return depositAccountRepository.findByIdAndHouseholdId(id, householdScopeService.requireHouseholdId())
                .orElseThrow(() -> new RessourceNotFoundException("DepositAccount", "id", id));
    }

    @Transactional
    public DepositAccount createDepositAccount(CreateDepositAccountRequest request) {
        User owner = currentUserService.getCurrentUser();
        Household household = householdScopeService.requireHousehold();
        DepositAccount account = new DepositAccount();
        account.setName(request.name());
        account.setCurrency(request.currency());
        account.setBalance(request.balance());
        account.setInterestRate(request.interestRate());
        account.setTerm(request.term());
        account.setRenewable(request.renewable());
        account.setOwner(owner);
        account.setHousehold(household);
        return depositAccountRepository.save(account);
    }

    @Transactional
    public void deleteDepositAccount(Long id) {
        DepositAccount account = depositAccountRepository
                .findByIdAndHouseholdId(id, householdScopeService.requireHouseholdId())
                .orElseThrow(() -> new RessourceNotFoundException("DepositAccount", "id", id));
        depositAccountRepository.delete(account);
    }
}

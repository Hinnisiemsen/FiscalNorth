package de.fiscalnorth.contract.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.contract.dto.CreateContractRequest;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.repository.ContractRepository;
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
public class ContractService {
    private final ContractRepository contractRepository;
    private final CurrentUserService currentUserService;
    private final HouseholdScopeService householdScopeService;

    public List<Contract> getAllContracts() {
        return contractRepository.findAllByHouseholdId(householdScopeService.requireHouseholdId());
    }

    public Contract getContractById(Long id) {
        return contractRepository.findByIdAndHouseholdId(id, householdScopeService.requireHouseholdId())
                .orElseThrow(() -> new RessourceNotFoundException("Contract", "id", id));
    }

    @Transactional
    public Contract createContract(CreateContractRequest request) {
        User owner = currentUserService.getCurrentUser();
        Household household = householdScopeService.requireHousehold();
        Contract contract = new Contract();
        contract.setName(request.name());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setAmount(request.amount());
        contract.setContractInterval(request.contractInterval());
        contract.setAutoDetected(request.autoDetected());
        contract.setOwner(owner);
        contract.setHousehold(household);
        return contractRepository.save(contract);
    }

    @Transactional
    public void deleteContract(Long id) {
        Contract contract = contractRepository.findByIdAndHouseholdId(id, householdScopeService.requireHouseholdId())
                .orElseThrow(() -> new RessourceNotFoundException("Contract", "id", id));
        contractRepository.delete(contract);
    }
}

package de.fiscalnorth.contract.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.contract.dto.CreateContractRequest;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.repository.ContractRepository;
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

    public List<Contract> getAllContracts() {
        return contractRepository.findAllByOwnerId(currentUserService.getCurrentUserId());
    }

    public Contract getContractById(Long id) {
        return contractRepository.findByIdAndOwnerId(id, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("Contract", "id", id));
    }

    @Transactional
    public Contract createContract(CreateContractRequest request) {
        User owner = currentUserService.getCurrentUser();
        Contract contract = new Contract();
        contract.setName(request.name());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setAmount(request.amount());
        contract.setContractInterval(request.contractInterval());
        contract.setAutoDetected(request.autoDetected());
        contract.setOwner(owner);
        return contractRepository.save(contract);
    }

    @Transactional
    public void deleteContract(Long id) {
        Contract contract = contractRepository.findByIdAndOwnerId(id, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("Contract", "id", id));
        contractRepository.delete(contract);
    }
}

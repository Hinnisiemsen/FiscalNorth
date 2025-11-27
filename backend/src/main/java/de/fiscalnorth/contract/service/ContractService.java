package de.fiscalnorth.contract.service;

import de.fiscalnorth.contract.dto.CreateContractRequest;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractService {
    private final ContractRepository contractRepository;

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Contract", "id", id));
    }

    @Transactional
    public Contract createContract(CreateContractRequest request) {
        Contract contract = new Contract();
        contract.setName(request.name());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setAmount(request.amount());
        contract.setContractInterval(request.contractInterval());
        contract.setAutoDetected(request.autoDetected());
        return contractRepository.save(contract);
    }

    @Transactional
    public void deleteContract(Long id) {
        if (!contractRepository.existsById(id)) {
            throw new RessourceNotFoundException("Contract", "id", id);
        }
        contractRepository.deleteById(id);
    }
}

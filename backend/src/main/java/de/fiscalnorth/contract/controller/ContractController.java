package de.fiscalnorth.contract.controller;

import de.fiscalnorth.contract.dto.CreateContractRequest;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import de.fiscalnorth.contract.service.ContractAnalysisService;

@RestController
@RequestMapping("/api/contract")
public class ContractController {
    private final ContractService contractService;
    private final ContractAnalysisService contractAnalysisService;

    public ContractController(ContractService contractService, ContractAnalysisService contractAnalysisService) {
        this.contractService = contractService;
        this.contractAnalysisService = contractAnalysisService;
    }

    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody @Valid CreateContractRequest request) {
        Contract contract = contractService.createContract(request);
        return new ResponseEntity<>(contract, HttpStatus.CREATED);
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeContracts() {
        int count = contractAnalysisService.analyzeAndCreateContracts();
        return ResponseEntity.ok("Analysis complete. Created " + count + " new contracts.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }
}

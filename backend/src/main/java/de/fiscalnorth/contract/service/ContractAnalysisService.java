package de.fiscalnorth.contract.service;

import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.model.ContractInterval;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractAnalysisService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ContractRepository contractRepository;

    @Transactional
    public int analyzeAndCreateContracts() {
        List<PaymentTransaction> allTransactions = paymentTransactionRepository.findAll();
        int createdContracts = 0;

        // Group by description and amount to find identical recurring payments (skip nulls - groupingBy rejects null keys)
        Map<String, Map<Double, List<PaymentTransaction>>> groupedTransactions = allTransactions.stream()
                .filter(t -> t.getDescription() != null && t.getAmount() != null)
                .collect(Collectors.groupingBy(PaymentTransaction::getDescription,
                        Collectors.groupingBy(t -> t.getAmount().doubleValue())));

        for (var entry : groupedTransactions.entrySet()) {
            String description = entry.getKey();
            for (var amountEntry : entry.getValue().entrySet()) {
                List<PaymentTransaction> transactions = amountEntry.getValue();

                // Simple heuristic: If it appears 3 or more times, it's likely a contract
                if (transactions.size() >= 3) {
                    // Check if contract already exists
                    boolean exists = contractRepository.findAll().stream()
                            .anyMatch(c -> c.getName() != null && c.getName().equalsIgnoreCase(description));

                    if (!exists) {
                        createContractFromTransactions(description, transactions);
                        createdContracts++;
                    }
                }
            }
        }
        return createdContracts;
    }

    private void createContractFromTransactions(String name, List<PaymentTransaction> transactions) {
        PaymentTransaction latest = transactions.get(transactions.size() - 1);

        Contract contract = new Contract();
        contract.setName(name);
        contract.setAmount(latest.getAmount());
        contract.setStartDate(transactions.get(0).getTransactionDate());
        contract.setEndDate(LocalDate.now().plusYears(1)); // Default to 1 year from now
        contract.setContractInterval(ContractInterval.MONTHLY); // Default to monthly for MVP
        contract.setAutoDetected(true);

        contractRepository.save(contract);
    }
}

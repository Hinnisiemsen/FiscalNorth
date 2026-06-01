package de.fiscalnorth.contract.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.model.ContractInterval;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.user.model.User;
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
    private final CurrentUserService currentUserService;

    @Transactional
    public int analyzeAndCreateContracts() {
        User owner = currentUserService.getCurrentUser();
        List<PaymentTransaction> allTransactions =
                paymentTransactionRepository.findAllByOwnerId(owner.getId());
        int createdContracts = 0;

        Map<String, Map<Double, List<PaymentTransaction>>> groupedTransactions = allTransactions.stream()
                .filter(t -> t.getDescription() != null && t.getAmount() != null)
                .collect(Collectors.groupingBy(PaymentTransaction::getDescription,
                        Collectors.groupingBy(t -> t.getAmount().doubleValue())));

        for (var entry : groupedTransactions.entrySet()) {
            String description = entry.getKey();
            for (var amountEntry : entry.getValue().entrySet()) {
                List<PaymentTransaction> transactions = amountEntry.getValue();

                if (transactions.size() >= 3) {
                    boolean exists = contractRepository.findAllByOwnerId(owner.getId()).stream()
                            .anyMatch(c -> c.getName() != null && c.getName().equalsIgnoreCase(description));

                    if (!exists) {
                        createContractFromTransactions(owner, description, transactions);
                        createdContracts++;
                    }
                }
            }
        }
        return createdContracts;
    }

    private void createContractFromTransactions(User owner, String name, List<PaymentTransaction> transactions) {
        PaymentTransaction latest = transactions.get(transactions.size() - 1);

        Contract contract = new Contract();
        contract.setName(name);
        contract.setAmount(latest.getAmount());
        contract.setStartDate(transactions.get(0).getTransactionDate());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setContractInterval(ContractInterval.MONTHLY);
        contract.setAutoDetected(true);
        contract.setOwner(owner);

        contractRepository.save(contract);
    }
}

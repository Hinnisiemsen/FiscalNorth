package de.fiscalnorth.ai.service;

import de.fiscalnorth.account.model.DepositAccount;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.budget.dto.BudgetWithUsage;
import de.fiscalnorth.budget.service.BudgetService;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class FinancialContextService {

    private final DepositAccountRepository depositAccountRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BudgetService budgetService;
    private final ContractRepository contractRepository;

    public String buildContextSnapshot() {
        List<DepositAccount> accounts = depositAccountRepository.findAll();
        BigDecimal totalBalance = accounts.stream()
                .map(DepositAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Contract> contracts = contractRepository.findAll();
        BigDecimal monthlyFixed = contracts.stream()
                .map(Contract::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PaymentTransaction> recent = paymentTransactionRepository
                .findAllByOrderByTransactionDateDesc(PageRequest.of(0, 15));

        List<BudgetWithUsage> budgets = budgetService.getBudgetsWithUsage().stream()
                .filter(b -> !b.endDate().isBefore(LocalDate.now().withDayOfMonth(1))
                        && !b.startDate().isAfter(LocalDate.now()))
                .limit(8)
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("Datum heute: ").append(LocalDate.now()).append("\n");
        sb.append("Gesamtsaldo Konten: ").append(totalBalance).append(" EUR\n");
        sb.append("Geschätzte monatliche Fixkosten (Verträge): ").append(monthlyFixed).append(" EUR\n");
        sb.append("Verfügbares Einkommen (Saldo - Fixkosten): ").append(totalBalance.subtract(monthlyFixed)).append(" EUR\n\n");

        sb.append("Konten:\n");
        for (DepositAccount a : accounts) {
            sb.append("- ").append(a.getName()).append(": ").append(a.getBalance()).append(" EUR\n");
        }

        sb.append("\nAktuelle Budgets:\n");
        for (BudgetWithUsage b : budgets) {
            sb.append("- ").append(b.name()).append(": ").append(b.spent()).append(" / ").append(b.limit())
                    .append(" EUR");
            if (b.categoryName() != null) {
                sb.append(" (Kategorie: ").append(b.categoryName()).append(")");
            }
            sb.append("\n");
        }

        sb.append("\nVerträge/Abos:\n");
        for (Contract c : contracts) {
            sb.append("- ").append(c.getName()).append(": ").append(c.getAmount()).append(" EUR/Monat\n");
        }

        sb.append("\nLetzte Transaktionen:\n");
        for (PaymentTransaction tx : recent) {
            String cat = tx.getCategory() != null ? tx.getCategory().getName() : "-";
            sb.append("- ").append(tx.getTransactionDate()).append(" | ")
                    .append(tx.getTransactionType()).append(" | ")
                    .append(tx.getAmount()).append(" EUR | ")
                    .append(tx.getDescription()).append(" | Kategorie: ").append(cat).append("\n");
        }

        return sb.toString();
    }
}

package de.fiscalnorth.account.model;

import de.fiscalnorth.xs2a.model.BankConsent;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@DiscriminatorValue("DEPOSIT")
public class DepositAccount extends Account {

    /** External ID from XS2A (resourceId) for bank sync deduplication */
    @Column(unique = true)
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_consent_id")
    private BankConsent bankConsent;

    private Double interestRate;        // Zinssatz
    private String term;                // Laufzeit (z.B. "12 Monate")
    private Boolean renewable;          // Automatische Verlängerung erlaubt
}

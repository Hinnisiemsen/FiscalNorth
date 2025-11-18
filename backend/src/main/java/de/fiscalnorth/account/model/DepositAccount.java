package de.fiscalnorth.account.model;

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
    private Double interestRate;        // Zinssatz
    private String term;                // Laufzeit (z.B. "12 Monate")
    private Boolean renewable;          // Automatische Verlängerung erlaubt
}

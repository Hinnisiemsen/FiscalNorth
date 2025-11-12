package de.hinni.haushaltsmanager.account.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@DiscriminatorValue("BANK")
public class BankAccount extends Account {
    private String bankName;
    private String iban;
    private String bic;
    private AccountType accountType;
}

package de.fiscalnorth.household.model;

import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Household extends BaseEntity {
    private String name;
}

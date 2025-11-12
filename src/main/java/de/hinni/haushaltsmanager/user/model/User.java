package de.hinni.haushaltsmanager.user.model;

import de.hinni.haushaltsmanager.shared.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User extends BaseEntity {
    protected String userName;
    protected String email;
    protected String passwordHash;
    protected UserRole userRole;
}

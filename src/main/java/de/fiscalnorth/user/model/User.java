package de.fiscalnorth.user.model;

import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Entity;
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

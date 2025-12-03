package de.fiscalnorth.user.model;

import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class User extends BaseEntity {
    protected String userName;
    protected String email;
    protected String passwordHash;
    protected UserRole userRole;
}

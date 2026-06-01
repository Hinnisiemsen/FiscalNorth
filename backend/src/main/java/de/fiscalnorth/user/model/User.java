package de.fiscalnorth.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class User extends BaseEntity {
    protected String userName;

    @Column(unique = true)
    protected String email;

    @JsonIgnore
    protected String passwordHash;

    @Enumerated(EnumType.STRING)
    protected UserRole userRole;

    @Column(unique = true)
    protected String googleSubjectId;

    @Enumerated(EnumType.STRING)
    protected AuthProvider authProvider = AuthProvider.LOCAL;

    protected String avatarUrl;

    protected String locale = "en";
}

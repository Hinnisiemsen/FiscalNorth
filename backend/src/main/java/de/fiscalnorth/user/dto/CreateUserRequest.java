package de.fiscalnorth.user.dto;

import de.fiscalnorth.user.model.UserRole;

public record CreateUserRequest(
        String userName,
        String email,
        String password,
        UserRole userRole) {
}

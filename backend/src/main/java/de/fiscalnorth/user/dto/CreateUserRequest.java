package de.fiscalnorth.user.dto;

import de.fiscalnorth.user.model.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
                @NotBlank String userName,
                @NotBlank @Email String email,
                @NotBlank @Size(min = 8) String password,
                @NotNull UserRole userRole) {
}

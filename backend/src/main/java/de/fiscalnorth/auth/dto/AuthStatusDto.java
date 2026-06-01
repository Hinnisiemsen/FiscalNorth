package de.fiscalnorth.auth.dto;

import de.fiscalnorth.user.model.AuthProvider;

public record AuthStatusDto(
        boolean authenticated,
        AuthProvider provider
) {
}

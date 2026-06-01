package de.fiscalnorth.user.dto;

import de.fiscalnorth.user.model.AuthProvider;

public record UserProfileDto(
        Long id,
        String userName,
        String email,
        String avatarUrl,
        AuthProvider authProvider,
        String locale
) {
}

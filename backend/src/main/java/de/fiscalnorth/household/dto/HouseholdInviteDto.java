package de.fiscalnorth.household.dto;

import java.time.Instant;

public record HouseholdInviteDto(
        Long id,
        String email,
        String token,
        Instant expiresAt,
        String status) {}

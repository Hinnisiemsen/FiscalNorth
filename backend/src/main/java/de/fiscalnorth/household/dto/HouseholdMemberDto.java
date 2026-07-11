package de.fiscalnorth.household.dto;

import java.time.Instant;

public record HouseholdMemberDto(
        Long userId,
        String userName,
        String email,
        String role,
        Instant joinedAt) {}

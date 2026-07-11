package de.fiscalnorth.household.dto;

import java.util.List;

public record HouseholdResponse(
        Long id,
        String name,
        List<HouseholdMemberDto> members,
        HouseholdInviteDto pendingInvite) {}

package de.fiscalnorth.household.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.model.HouseholdMember;
import de.fiscalnorth.household.repository.HouseholdMemberRepository;
import de.fiscalnorth.shared.LocalizedException;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseholdScopeService {

    private final CurrentUserService currentUserService;
    private final HouseholdMemberRepository householdMemberRepository;

    public Household requireHousehold() {
        User user = currentUserService.getCurrentUser();
        return householdMemberRepository.findByUserId(user.getId())
                .map(HouseholdMember::getHousehold)
                .orElseThrow(() -> new LocalizedException("error.household.notFound"));
    }

    public Long requireHouseholdId() {
        return requireHousehold().getId();
    }

    public Household requireHouseholdForUser(User user) {
        return householdMemberRepository.findByUserId(user.getId())
                .map(HouseholdMember::getHousehold)
                .orElseThrow(() -> new LocalizedException("error.household.notFound"));
    }

    public Long requireHouseholdIdForUser(User user) {
        return requireHouseholdForUser(user).getId();
    }
}

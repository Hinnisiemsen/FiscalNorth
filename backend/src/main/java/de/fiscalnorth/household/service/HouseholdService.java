package de.fiscalnorth.household.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.household.dto.HouseholdInviteDto;
import de.fiscalnorth.household.dto.HouseholdMemberDto;
import de.fiscalnorth.household.dto.HouseholdResponse;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.model.HouseholdInvite;
import de.fiscalnorth.household.model.HouseholdInviteStatus;
import de.fiscalnorth.household.model.HouseholdMember;
import de.fiscalnorth.household.model.HouseholdMemberRole;
import de.fiscalnorth.household.repository.HouseholdInviteRepository;
import de.fiscalnorth.household.repository.HouseholdMemberRepository;
import de.fiscalnorth.household.repository.HouseholdRepository;
import de.fiscalnorth.shared.LocalizedException;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private static final int MAX_MEMBERS = 2;
    private static final int INVITE_DAYS = 7;

    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final HouseholdInviteRepository householdInviteRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public Household createHouseholdForUser(User user, String name) {
        if (householdMemberRepository.findByUserId(user.getId()).isPresent()) {
            return householdMemberRepository.findByUserId(user.getId()).orElseThrow().getHousehold();
        }
        Household household = new Household();
        household.setName(name != null && !name.isBlank() ? name : user.getUserName() + "'s Household");
        household = householdRepository.save(household);

        HouseholdMember member = new HouseholdMember();
        member.setHousehold(household);
        member.setUser(user);
        member.setRole(HouseholdMemberRole.OWNER);
        householdMemberRepository.save(member);
        return household;
    }

    public HouseholdResponse getMyHousehold() {
        User user = currentUserService.getCurrentUser();
        HouseholdMember membership = householdMemberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new LocalizedException("error.household.notFound"));
        Household household = membership.getHousehold();
        List<HouseholdMemberDto> members = householdMemberRepository.findAllByHouseholdId(household.getId()).stream()
                .map(m -> new HouseholdMemberDto(
                        m.getUser().getId(),
                        m.getUser().getUserName(),
                        m.getUser().getEmail(),
                        m.getRole().name(),
                        m.getJoinedAt()))
                .toList();
        HouseholdInviteDto pendingInvite = householdInviteRepository
                .findAllByHouseholdIdAndStatus(household.getId(), HouseholdInviteStatus.PENDING).stream()
                .findFirst()
                .map(i -> new HouseholdInviteDto(
                        i.getId(), i.getEmail(), i.getToken(), i.getExpiresAt(), i.getStatus().name()))
                .orElse(null);
        return new HouseholdResponse(household.getId(), household.getName(), members, pendingInvite);
    }

    @Transactional
    public HouseholdInviteDto invitePartner(String email) {
        User user = currentUserService.getCurrentUser();
        HouseholdMember membership = householdMemberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new LocalizedException("error.household.notFound"));
        if (membership.getRole() != HouseholdMemberRole.OWNER) {
            throw new LocalizedException("error.household.notOwner");
        }
        Household household = membership.getHousehold();
        if (householdMemberRepository.countByHouseholdId(household.getId()) >= MAX_MEMBERS) {
            throw new LocalizedException("error.household.full");
        }
        if (householdInviteRepository.countByHouseholdIdAndStatus(household.getId(), HouseholdInviteStatus.PENDING) > 0) {
            throw new LocalizedException("error.household.invitePending");
        }
        if (userRepository.findByEmail(email).flatMap(u -> householdMemberRepository.findByUserId(u.getId())).isPresent()) {
            throw new LocalizedException("error.household.alreadyMember");
        }

        HouseholdInvite invite = new HouseholdInvite();
        invite.setHousehold(household);
        invite.setEmail(email.trim().toLowerCase());
        invite.setToken(UUID.randomUUID().toString());
        invite.setExpiresAt(Instant.now().plus(INVITE_DAYS, ChronoUnit.DAYS));
        invite.setStatus(HouseholdInviteStatus.PENDING);
        invite = householdInviteRepository.save(invite);
        return new HouseholdInviteDto(
                invite.getId(), invite.getEmail(), invite.getToken(), invite.getExpiresAt(), invite.getStatus().name());
    }

    @Transactional
    public HouseholdResponse acceptInvite(String token) {
        User user = currentUserService.getCurrentUser();
        if (householdMemberRepository.findByUserId(user.getId()).isPresent()) {
            throw new LocalizedException("error.household.alreadyJoined");
        }
        HouseholdInvite invite = householdInviteRepository.findByTokenAndStatus(token, HouseholdInviteStatus.PENDING)
                .orElseThrow(() -> new LocalizedException("error.household.inviteInvalid"));
        if (invite.getExpiresAt().isBefore(Instant.now())) {
            invite.setStatus(HouseholdInviteStatus.EXPIRED);
            throw new LocalizedException("error.household.inviteExpired");
        }
        if (!invite.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new LocalizedException("error.household.inviteEmailMismatch");
        }
        if (householdMemberRepository.countByHouseholdId(invite.getHousehold().getId()) >= MAX_MEMBERS) {
            throw new LocalizedException("error.household.full");
        }

        HouseholdMember member = new HouseholdMember();
        member.setHousehold(invite.getHousehold());
        member.setUser(user);
        member.setRole(HouseholdMemberRole.MEMBER);
        householdMemberRepository.save(member);
        invite.setStatus(HouseholdInviteStatus.ACCEPTED);
        return getMyHousehold();
    }
}

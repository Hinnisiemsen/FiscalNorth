package de.fiscalnorth.household.repository;

import de.fiscalnorth.household.model.HouseholdInvite;
import de.fiscalnorth.household.model.HouseholdInviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdInviteRepository extends JpaRepository<HouseholdInvite, Long> {
    Optional<HouseholdInvite> findByTokenAndStatus(String token, HouseholdInviteStatus status);

    long countByHouseholdIdAndStatus(Long householdId, HouseholdInviteStatus status);

    List<HouseholdInvite> findAllByHouseholdIdAndStatus(Long householdId, HouseholdInviteStatus status);
}

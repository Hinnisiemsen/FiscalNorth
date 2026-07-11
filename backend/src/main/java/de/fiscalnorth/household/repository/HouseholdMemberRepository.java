package de.fiscalnorth.household.repository;

import de.fiscalnorth.household.model.HouseholdMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {
    Optional<HouseholdMember> findByUserId(Long userId);

    long countByHouseholdId(Long householdId);

    List<HouseholdMember> findAllByHouseholdId(Long householdId);

    boolean existsByHouseholdIdAndUserId(Long householdId, Long userId);
}

package de.fiscalnorth.xs2a.repository;

import de.fiscalnorth.xs2a.model.BankConsent;
import de.fiscalnorth.xs2a.model.BankConsent.ConsentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankConsentRepository extends JpaRepository<BankConsent, Long> {

    Optional<BankConsent> findByConsentId(String consentId);

    List<BankConsent> findByPsuId(String psuId);

    List<BankConsent> findByPsuIdAndStatus(String psuId, ConsentStatus status);
}

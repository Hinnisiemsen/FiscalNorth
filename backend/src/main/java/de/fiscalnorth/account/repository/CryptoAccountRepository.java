package de.fiscalnorth.account.repository;

import de.fiscalnorth.account.model.CryptoAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoAccountRepository extends JpaRepository<CryptoAccount, Long> {
    List<CryptoAccount> findAllByOwnerId(Long ownerId);

    Optional<CryptoAccount> findByIdAndOwnerId(Long id, Long ownerId);
}

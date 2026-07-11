package de.fiscalnorth.account.service;

import de.fiscalnorth.account.dto.CreateCryptoAccountRequest;
import de.fiscalnorth.account.model.CryptoAccount;
import de.fiscalnorth.account.repository.CryptoAccountRepository;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CryptoAccountService {

    private final CryptoAccountRepository cryptoAccountRepository;
    private final CurrentUserService currentUserService;

    public List<CryptoAccount> getAllCryptoAccounts() {
        return cryptoAccountRepository.findAllByOwnerId(currentUserService.getCurrentUserId());
    }

    public CryptoAccount getCryptoAccountById(Long id) {
        return cryptoAccountRepository.findByIdAndOwnerId(id, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new RessourceNotFoundException("CryptoAccount", "id", id));
    }

    @Transactional
    public CryptoAccount createCryptoAccount(CreateCryptoAccountRequest request) {
        User owner = currentUserService.getCurrentUser();
        CryptoAccount account = new CryptoAccount();
        account.setName(request.name());
        account.setWalletAddress(request.walletAddress());
        account.setProvider(request.provider());
        account.setCurrency(request.currency());
        account.setBalance(request.balance());
        account.setOwner(owner);
        return cryptoAccountRepository.save(account);
    }
}

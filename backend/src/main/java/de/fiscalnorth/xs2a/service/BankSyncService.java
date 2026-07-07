package de.fiscalnorth.xs2a.service;

import de.fiscalnorth.account.model.DepositAccount;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.billing.model.PremiumFeature;
import de.fiscalnorth.billing.service.EntitlementService;
import de.fiscalnorth.shared.Messages;
import de.fiscalnorth.shared.SupportedCurrency;
import de.fiscalnorth.transaction.model.PaymentTransaction;
import de.fiscalnorth.transaction.model.TransactionType;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.xs2a.config.Xs2aProperties;
import de.fiscalnorth.xs2a.dto.BankConsentDto;
import de.fiscalnorth.xs2a.dto.BankSyncStatusDto;
import de.fiscalnorth.xs2a.dto.CreateConsentResponseDto;
import de.fiscalnorth.xs2a.model.BankConsent;
import de.fiscalnorth.xs2a.model.BankConsent.ConsentStatus;
import de.fiscalnorth.xs2a.repository.BankConsentRepository;
import io.finapi.xs2a.ApiException;
import io.finapi.xs2a.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BankSyncService {

    private static final int CONSENT_VALID_DAYS = 90;
    private static final int FREQUENCY_PER_DAY = 4;
    private static final int SYNC_DAYS_BACK = 90;

    private final Xs2aProperties properties;
    private final BankConsentRepository bankConsentRepository;
    private final DepositAccountRepository depositAccountRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final Messages messages;
    private final CurrentUserService currentUserService;
    private final EntitlementService entitlementService;

    @Autowired(required = false)
    private io.finapi.xs2a.api.AccountInformationServiceAisApi aisApi;

    public BankSyncService(Xs2aProperties properties,
                           BankConsentRepository bankConsentRepository,
                           DepositAccountRepository depositAccountRepository,
                           PaymentTransactionRepository paymentTransactionRepository,
                           Messages messages,
                           CurrentUserService currentUserService,
                           EntitlementService entitlementService) {
        this.properties = properties;
        this.bankConsentRepository = bankConsentRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.messages = messages;
        this.currentUserService = currentUserService;
        this.entitlementService = entitlementService;
    }

    public BankSyncStatusDto getStatus() {
        if (!properties.isEnabled()) {
            return BankSyncStatusDto.unavailable(messages.get("bankSync.unavailable"));
        }
        return BankSyncStatusDto.available(messages.get("bankSync.ready"));
    }

    @Transactional
    public CreateConsentResponseDto createConsent() {
        entitlementService.requireFeature(
                currentUserService.getCurrentUser(), PremiumFeature.BANK_SYNC);
        if (!properties.isEnabled() || aisApi == null) {
            return new CreateConsentResponseDto(null, null, messages.get("bankSync.notConfigured"));
        }

        try {
            User owner = currentUserService.getCurrentUser();
            String psuId = owner.getId().toString();
            UUID xRequestId = UUID.randomUUID();
            Consents consents = buildConsentsBody();
            URI redirectUri = URI.create(properties.getRedirectUri());

            ConsentsResponse201 response = aisApi.createConsent(
                    xRequestId,
                    consents,
                    null, null, null,
                    psuId,
                    null, null, null,
                    true,
                    false,
                    redirectUri,
                    null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null);

            String consentId = response.getConsentId();
            BankConsent bankConsent = new BankConsent();
            bankConsent.setConsentId(consentId);
            bankConsent.setOwner(owner);
            bankConsent.setStatus(ConsentStatus.PENDING);
            bankConsent.setValidUntil(LocalDate.now().plusDays(CONSENT_VALID_DAYS));
            bankConsentRepository.save(bankConsent);

            StartScaprocessResponse authResponse = aisApi.startConsentAuthorisation(
                    consentId,
                    UUID.randomUUID(),
                    null, null, null,
                    psuId,
                    null, null, null,
                    true, redirectUri, null, null, null,
                    null, null, null, null, null, null, null, null, null, null,
                    null);

            String redirectUrl = null;
            if (authResponse.getLinks() != null && authResponse.getLinks().getScaRedirect() != null) {
                redirectUrl = authResponse.getLinks().getScaRedirect().getHref();
            }

            return new CreateConsentResponseDto(consentId, redirectUrl,
                    redirectUrl != null
                            ? messages.get("bankSync.redirectAuth")
                            : messages.get("bankSync.consentCreated"));
        } catch (ApiException e) {
            return new CreateConsentResponseDto(null, null, messages.get("bankSync.xs2aError", e.getMessage()));
        }
    }

    private Consents buildConsentsBody() {
        AccountAccess access = new AccountAccess();
        access.setAvailableAccounts(AccountAccess.AvailableAccountsEnum.ALL_ACCOUNTS);
        access.setAvailableAccountsWithBalance(AccountAccess.AvailableAccountsWithBalanceEnum.ALL_ACCOUNTS);
        access.setAllPsd2(AccountAccess.AllPsd2Enum.ALL_ACCOUNTS);

        Consents consents = new Consents();
        consents.setAccess(access);
        consents.setRecurringIndicator(true);
        consents.setValidUntil(LocalDate.now().plusDays(CONSENT_VALID_DAYS));
        consents.setFrequencyPerDay(FREQUENCY_PER_DAY);
        return consents;
    }

    @Transactional
    public String handleCallback(String consentId) {
        entitlementService.requireFeature(
                currentUserService.getCurrentUser(), PremiumFeature.BANK_SYNC);
        if (!properties.isEnabled() || aisApi == null) {
            return "/bank-sync?error=not_configured";
        }

        Optional<BankConsent> opt = bankConsentRepository.findByConsentIdAndOwnerId(
                consentId, currentUserService.getCurrentUserId());
        if (opt.isEmpty()) {
            return "/bank-sync?error=consent_not_found";
        }

        BankConsent consent = opt.get();
        try {
            ConsentStatusResponse200 statusResp = aisApi.getConsentStatus(consentId, UUID.randomUUID(), null, null, null, null, null, null, null, null, null, null, null, null, null);
            String statusVal = statusResp != null && statusResp.getConsentStatus() != null ? statusResp.getConsentStatus().getValue() : null;
            if ("valid".equalsIgnoreCase(statusVal)) {
                consent.setStatus(ConsentStatus.VALID);
                bankConsentRepository.save(consent);
                syncAccountsAndTransactions(consentId);
                return "/accounts?synced=true";
            }
        } catch (ApiException e) {
            return "/bank-sync?error=" + e.getMessage();
        }
        return "/bank-sync?error=consent_not_valid";
    }

    @Transactional
    public SyncResultDto sync(String consentId) {
        entitlementService.requireFeature(
                currentUserService.getCurrentUser(), PremiumFeature.BANK_SYNC);
        if (!properties.isEnabled() || aisApi == null) {
            return new SyncResultDto(false, 0, 0, messages.get("bankSync.notConfigured"));
        }

        Optional<BankConsent> opt = bankConsentRepository.findByConsentIdAndOwnerId(
                consentId, currentUserService.getCurrentUserId());
        if (opt.isEmpty()) {
            return new SyncResultDto(false, 0, 0, messages.get("bankSync.consentNotFound"));
        }

        BankConsent consent = opt.get();
        if (consent.getStatus() != ConsentStatus.VALID) {
            return new SyncResultDto(false, 0, 0, messages.get("bankSync.consentNotValid", consent.getStatus()));
        }

        return syncAccountsAndTransactions(consentId);
    }

    private SyncResultDto syncAccountsAndTransactions(String consentId) {
        int accountsCreated = 0;
        int transactionsCreated = 0;

        try {
            AccountList accountList = aisApi.getAccountList(UUID.randomUUID(), consentId, true, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
            if (accountList == null || accountList.getAccounts() == null) {
                return new SyncResultDto(true, 0, 0, messages.get("bankSync.noAccounts"));
            }

            BankConsent consent = bankConsentRepository.findByConsentId(consentId).orElseThrow();
            LocalDate dateFrom = LocalDate.now().minusDays(SYNC_DAYS_BACK);

            for (AccountDetails acc : accountList.getAccounts()) {
                String resourceId = acc.getResourceId();
                boolean wasNew = resourceId != null && depositAccountRepository
                        .findByOwnerIdAndExternalId(consent.getOwner().getId(), resourceId).isEmpty();
                DepositAccount depositAccount = syncAccount(acc, consent);
                if (depositAccount != null && wasNew) {
                    accountsCreated++;
                }

                if (depositAccount != null && acc.getResourceId() != null) {
                    try {
                        TransactionsResponseV1200Json txResp = aisApi.getTransactionListV1(
                                acc.getResourceId(),
                                "booked",
                                UUID.randomUUID(),
                                consentId,
                                dateFrom.toString(),
                                null, null, null, null, null,
                                null, null, null, null, null, null, null, null, null, null, null, null, null);

                        if (txResp != null && txResp.getTransactions() != null && txResp.getTransactions().getBooked() != null) {
                            for (TransactionsV1 tx : txResp.getTransactions().getBooked()) {
                                if (createTransactionIfNew(tx, depositAccount)) {
                                    transactionsCreated++;
                                }
                            }
                        }
                    } catch (ApiException e) {
                        // continue with next account
                    }
                }
            }

            return new SyncResultDto(true, accountsCreated, transactionsCreated, messages.get("bankSync.syncCompleted"));
        } catch (ApiException e) {
            return new SyncResultDto(false, 0, 0, messages.get("bankSync.syncFailed", e.getMessage()));
        }
    }

    private DepositAccount syncAccount(AccountDetails acc, BankConsent consent) {
        String resourceId = acc.getResourceId();
        if (resourceId == null) return null;

        return depositAccountRepository.findByOwnerIdAndExternalId(consent.getOwner().getId(), resourceId)
                .map(existing -> {
                    if (acc.getCurrency() != null) {
                        existing.setCurrency(mapCurrency(acc.getCurrency()));
                    }
                    BigDecimal balance = getBalanceFromAccount(acc);
                    if (balance != null) existing.setBalance(balance);
                    return depositAccountRepository.save(existing);
                })
                .orElseGet(() -> {
                    DepositAccount newAcc = new DepositAccount();
                    newAcc.setExternalId(resourceId);
                    newAcc.setBankConsent(consent);
                    newAcc.setOwner(consent.getOwner());
                    newAcc.setName(acc.getIban() != null ? acc.getIban() : acc.getName() != null ? acc.getName() : "Account " + resourceId);
                    newAcc.setCurrency(acc.getCurrency() != null ? mapCurrency(acc.getCurrency()) : SupportedCurrency.EURO);
                    newAcc.setBalance(getBalanceFromAccount(acc) != null ? getBalanceFromAccount(acc) : BigDecimal.ZERO);
                    return depositAccountRepository.save(newAcc);
                });
    }

    private BigDecimal getBalanceFromAccount(AccountDetails acc) {
        if (acc.getBalances() != null && !acc.getBalances().isEmpty()) {
            for (Balance b : acc.getBalances()) {
                if (b != null && b.getBalanceAmount() != null && b.getBalanceAmount().getAmount() != null) {
                    return b.getBalanceAmount().getAmount();
                }
            }
        }
        return null;
    }

    private SupportedCurrency mapCurrency(String currency) {
        if (currency == null) return SupportedCurrency.EURO;
        return switch (currency.toUpperCase()) {
            case "USD" -> SupportedCurrency.USD;
            default -> SupportedCurrency.EURO;
        };
    }

    private boolean createTransactionIfNew(TransactionsV1 tx, DepositAccount account) {
        String hash = buildImportHash(tx);
        if (paymentTransactionRepository.existsByOwnerIdAndImportHash(account.getOwner().getId(), hash)) {
            return false;
        }

        PaymentTransaction pt = new PaymentTransaction();
        BigDecimal rawAmount = tx.getTransactionAmount() != null && tx.getTransactionAmount().getAmount() != null ? tx.getTransactionAmount().getAmount() : BigDecimal.ZERO;
        pt.setAmount(rawAmount.abs());
        pt.setTransactionType(rawAmount.signum() >= 0 ? TransactionType.Income : TransactionType.Expense);
        pt.setTransactionDate(tx.getBookingDate() != null ? tx.getBookingDate() : LocalDate.now());
        pt.setDescription(buildDescription(tx));
        pt.setImportHash(hash);
        pt.setOwner(account.getOwner());
        paymentTransactionRepository.save(pt);
        return true;
    }

    private String buildImportHash(TransactionsV1 tx) {
        if (tx.getTransactionId() != null && !tx.getTransactionId().isBlank()) {
            return "xs2a-" + tx.getTransactionId();
        }
        String date = tx.getBookingDate() != null ? tx.getBookingDate().toString() : "";
        String amount = tx.getTransactionAmount() != null && tx.getTransactionAmount().getAmount() != null ? tx.getTransactionAmount().getAmount().toString() : "";
        String desc = buildDescription(tx);
        return "xs2a-" + sha256(date + "|" + amount + "|" + desc);
    }

    private String buildDescription(TransactionsV1 tx) {
        if (tx.getCreditorName() != null) return tx.getCreditorName();
        if (tx.getDebtorName() != null) return tx.getDebtorName();
        if (tx.getRemittanceInformationUnstructured() != null && !tx.getRemittanceInformationUnstructured().isEmpty()) {
            return String.join(" ", tx.getRemittanceInformationUnstructured());
        }
        return tx.getTransactionId() != null ? tx.getTransactionId() : "XS2A transaction";
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "xs2a-" + UUID.randomUUID();
        }
    }

    public List<BankConsentDto> getConsents() {
        return bankConsentRepository.findByOwnerId(currentUserService.getCurrentUserId()).stream()
                .map(BankConsentDto::from)
                .collect(Collectors.toList());
    }

    public record SyncResultDto(boolean success, int accountsCreated, int transactionsCreated, String message) {}
}

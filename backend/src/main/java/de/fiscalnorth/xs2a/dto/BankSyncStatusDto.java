package de.fiscalnorth.xs2a.dto;

/**
 * Status of the XS2A bank sync integration.
 */
public record BankSyncStatusDto(
        boolean available,
        String message
) {
    public static BankSyncStatusDto available(String message) {
        return new BankSyncStatusDto(true, message);
    }

    public static BankSyncStatusDto unavailable(String message) {
        return new BankSyncStatusDto(false, message);
    }
}

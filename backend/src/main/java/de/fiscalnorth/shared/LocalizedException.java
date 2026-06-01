package de.fiscalnorth.shared;

import lombok.Getter;

@Getter
public class LocalizedException extends RuntimeException {

    private final String messageCode;
    private final Object[] messageArgs;

    public LocalizedException(String messageCode, Object... messageArgs) {
        super(messageCode);
        this.messageCode = messageCode;
        this.messageArgs = messageArgs;
    }
}

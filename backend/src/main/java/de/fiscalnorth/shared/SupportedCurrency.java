package de.fiscalnorth.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SupportedCurrency {
    EURO("EUR"),
    USD("USD");

    private final String code;

    SupportedCurrency(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static SupportedCurrency from(String s) {
        if (s == null) return null;
        for (SupportedCurrency c : values()) {
            if (c.code.equalsIgnoreCase(s) || c.name().equalsIgnoreCase(s)) return c;
        }
        throw new LocalizedException("error.currency.unknown", s);
    }
}

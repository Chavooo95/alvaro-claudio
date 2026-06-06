package com.boardgames.catalog.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public record Money(BigDecimal amount, String currency) {

    private static final Pattern ISO_4217 = Pattern.compile("^[A-Z]{3}$");

    public Money {
        Objects.requireNonNull(amount, "Money.amount must not be null");
        Objects.requireNonNull(currency, "Money.currency must not be null");
        if (amount.signum() < 0) {
            throw new InvalidGameException("Money.amount must not be negative, got " + amount);
        }
        if (!ISO_4217.matcher(currency).matches()) {
            throw new InvalidGameException("Money.currency must be a 3-letter ISO 4217 code, got '" + currency + "'");
        }
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
}

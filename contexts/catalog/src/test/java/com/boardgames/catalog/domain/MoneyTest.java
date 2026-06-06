package com.boardgames.catalog.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void rejects_negative_amount() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1.00"), "EUR"))
                .isInstanceOf(InvalidGameException.class);
    }

    @Test
    void rejects_invalid_currency_code() {
        assertThatThrownBy(() -> Money.of(BigDecimal.ONE, "euro"))
                .isInstanceOf(InvalidGameException.class);
        assertThatThrownBy(() -> Money.of(BigDecimal.ONE, "EU"))
                .isInstanceOf(InvalidGameException.class);
    }
}

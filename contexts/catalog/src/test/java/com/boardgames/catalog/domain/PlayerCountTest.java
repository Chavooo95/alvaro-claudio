package com.boardgames.catalog.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerCountTest {

    @Test
    void rejects_min_less_than_one() {
        assertThatThrownBy(() -> PlayerCount.of(0, 4))
                .isInstanceOf(InvalidGameException.class);
    }

    @Test
    void rejects_max_lower_than_min() {
        assertThatThrownBy(() -> PlayerCount.of(4, 2))
                .isInstanceOf(InvalidGameException.class);
    }
}

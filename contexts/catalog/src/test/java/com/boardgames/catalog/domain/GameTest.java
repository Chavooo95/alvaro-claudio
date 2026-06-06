package com.boardgames.catalog.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    @Test
    void creates_a_valid_game() {
        Game game = Game.create(
                GameId.newId(),
                "Catán",
                "Klaus Teuber",
                PlayerCount.of(3, 4),
                10,
                90,
                Money.of(new BigDecimal("39.95"), "EUR")
        );

        assertThat(game.title()).isEqualTo("Catán");
        assertThat(game.designer()).isEqualTo("Klaus Teuber");
        assertThat(game.playerCount().supports(4)).isTrue();
        assertThat(game.playerCount().supports(2)).isFalse();
    }

    @Test
    void rejects_blank_title() {
        assertThatThrownBy(() -> Game.create(
                GameId.newId(), "  ", "Klaus Teuber",
                PlayerCount.of(3, 4), 10, 90,
                Money.of(BigDecimal.TEN, "EUR")
        )).isInstanceOf(InvalidGameException.class);
    }

    @Test
    void rejects_non_positive_duration() {
        assertThatThrownBy(() -> Game.create(
                GameId.newId(), "Bomb", "Anon",
                PlayerCount.of(2, 6), 8, 0,
                Money.of(BigDecimal.TEN, "EUR")
        )).isInstanceOf(InvalidGameException.class);
    }

    @Test
    void updates_details_in_place() {
        Game game = Game.create(
                GameId.newId(), "Trivial", "Horn Abbot",
                PlayerCount.of(2, 6), 12, 60,
                Money.of(new BigDecimal("29.99"), "EUR")
        );

        game.updateDetails(
                "Trivial Pursuit", "Horn Abbot",
                PlayerCount.of(2, 8), 12, 75,
                Money.of(new BigDecimal("34.50"), "EUR")
        );

        assertThat(game.title()).isEqualTo("Trivial Pursuit");
        assertThat(game.durationMinutes()).isEqualTo(75);
        assertThat(game.playerCount().max()).isEqualTo(8);
        assertThat(game.price().amount()).isEqualByComparingTo("34.50");
    }
}

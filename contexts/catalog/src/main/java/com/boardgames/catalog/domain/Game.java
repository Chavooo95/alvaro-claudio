package com.boardgames.catalog.domain;

import java.util.Objects;

public final class Game {

    private final GameId id;
    private String title;
    private String designer;
    private PlayerCount playerCount;
    private int minAge;
    private int durationMinutes;
    private Money price;

    private Game(GameId id, String title, String designer, PlayerCount playerCount,
                 int minAge, int durationMinutes, Money price) {
        this.id = Objects.requireNonNull(id, "Game.id must not be null");
        applyDetails(title, designer, playerCount, minAge, durationMinutes, price);
    }

    public static Game create(GameId id, String title, String designer, PlayerCount playerCount,
                              int minAge, int durationMinutes, Money price) {
        return new Game(id, title, designer, playerCount, minAge, durationMinutes, price);
    }

    public static Game reconstruct(GameId id, String title, String designer, PlayerCount playerCount,
                                   int minAge, int durationMinutes, Money price) {
        return new Game(id, title, designer, playerCount, minAge, durationMinutes, price);
    }

    public void updateDetails(String title, String designer, PlayerCount playerCount,
                              int minAge, int durationMinutes, Money price) {
        applyDetails(title, designer, playerCount, minAge, durationMinutes, price);
    }

    private void applyDetails(String title, String designer, PlayerCount playerCount,
                              int minAge, int durationMinutes, Money price) {
        if (title == null || title.isBlank()) {
            throw new InvalidGameException("Game.title must not be blank");
        }
        if (designer == null || designer.isBlank()) {
            throw new InvalidGameException("Game.designer must not be blank");
        }
        if (minAge < 0) {
            throw new InvalidGameException("Game.minAge must be >= 0, got " + minAge);
        }
        if (durationMinutes <= 0) {
            throw new InvalidGameException("Game.durationMinutes must be > 0, got " + durationMinutes);
        }
        this.title = title.trim();
        this.designer = designer.trim();
        this.playerCount = Objects.requireNonNull(playerCount, "Game.playerCount must not be null");
        this.minAge = minAge;
        this.durationMinutes = durationMinutes;
        this.price = Objects.requireNonNull(price, "Game.price must not be null");
    }

    public GameId id() { return id; }
    public String title() { return title; }
    public String designer() { return designer; }
    public PlayerCount playerCount() { return playerCount; }
    public int minAge() { return minAge; }
    public int durationMinutes() { return durationMinutes; }
    public Money price() { return price; }

    @Override
    public boolean equals(Object other) {
        return other instanceof Game g && id.equals(g.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

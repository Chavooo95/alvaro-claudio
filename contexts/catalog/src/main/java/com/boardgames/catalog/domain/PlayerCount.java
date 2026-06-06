package com.boardgames.catalog.domain;

public record PlayerCount(int min, int max) {

    public PlayerCount {
        if (min < 1) {
            throw new InvalidGameException("PlayerCount.min must be >= 1, got " + min);
        }
        if (max < min) {
            throw new InvalidGameException("PlayerCount.max (" + max + ") must be >= min (" + min + ")");
        }
    }

    public static PlayerCount of(int min, int max) {
        return new PlayerCount(min, max);
    }

    public boolean supports(int players) {
        return players >= min && players <= max;
    }
}

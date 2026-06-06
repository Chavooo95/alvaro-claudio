package com.boardgames.catalog.domain;

import java.util.Objects;
import java.util.UUID;

public record GameId(UUID value) {
    public GameId {
        Objects.requireNonNull(value, "GameId.value must not be null");
    }

    public static GameId newId() {
        return new GameId(UUID.randomUUID());
    }

    public static GameId of(String raw) {
        try {
            return new GameId(UUID.fromString(raw));
        } catch (IllegalArgumentException ex) {
            throw new InvalidGameException("GameId must be a valid UUID, got '" + raw + "'");
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

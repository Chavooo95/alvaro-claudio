package com.boardgames.catalog.domain;

public class GameNotFoundException extends CatalogDomainException {
    public GameNotFoundException(GameId id) {
        super("Game not found: " + id);
    }
}

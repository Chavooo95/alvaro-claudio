package com.boardgames.catalog.infrastructure.rest.dto;

import com.boardgames.catalog.domain.Game;

import java.math.BigDecimal;
import java.util.UUID;

public record GameResponse(
        UUID id,
        String title,
        String designer,
        int minPlayers,
        int maxPlayers,
        int minAge,
        int durationMinutes,
        BigDecimal priceAmount,
        String priceCurrency
) {
    public static GameResponse from(Game game) {
        return new GameResponse(
                game.id().value(),
                game.title(),
                game.designer(),
                game.playerCount().min(),
                game.playerCount().max(),
                game.minAge(),
                game.durationMinutes(),
                game.price().amount(),
                game.price().currency()
        );
    }
}

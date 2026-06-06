package com.boardgames.catalog.application.command;

import java.math.BigDecimal;

public record CreateGameCommand(
        String title,
        String designer,
        int minPlayers,
        int maxPlayers,
        int minAge,
        int durationMinutes,
        BigDecimal priceAmount,
        String priceCurrency
) {
}

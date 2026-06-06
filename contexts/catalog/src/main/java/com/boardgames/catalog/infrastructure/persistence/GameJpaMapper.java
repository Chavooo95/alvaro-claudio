package com.boardgames.catalog.infrastructure.persistence;

import com.boardgames.catalog.domain.Game;
import com.boardgames.catalog.domain.GameId;
import com.boardgames.catalog.domain.Money;
import com.boardgames.catalog.domain.PlayerCount;

final class GameJpaMapper {

    private GameJpaMapper() {
    }

    static GameJpaEntity toEntity(Game game) {
        return new GameJpaEntity(
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

    static Game toDomain(GameJpaEntity entity) {
        return Game.reconstruct(
                new GameId(entity.getId()),
                entity.getTitle(),
                entity.getDesigner(),
                PlayerCount.of(entity.getMinPlayers(), entity.getMaxPlayers()),
                entity.getMinAge(),
                entity.getDurationMinutes(),
                Money.of(entity.getPriceAmount(), entity.getPriceCurrency())
        );
    }
}

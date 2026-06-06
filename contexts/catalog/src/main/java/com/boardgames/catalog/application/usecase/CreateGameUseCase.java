package com.boardgames.catalog.application.usecase;

import com.boardgames.catalog.application.command.CreateGameCommand;
import com.boardgames.catalog.domain.Game;
import com.boardgames.catalog.domain.GameId;
import com.boardgames.catalog.domain.GameRepository;
import com.boardgames.catalog.domain.Money;
import com.boardgames.catalog.domain.PlayerCount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateGameUseCase {

    private final GameRepository repository;

    public CreateGameUseCase(GameRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GameId execute(CreateGameCommand command) {
        Game game = Game.create(
                GameId.newId(),
                command.title(),
                command.designer(),
                PlayerCount.of(command.minPlayers(), command.maxPlayers()),
                command.minAge(),
                command.durationMinutes(),
                Money.of(command.priceAmount(), command.priceCurrency())
        );
        repository.save(game);
        return game.id();
    }
}

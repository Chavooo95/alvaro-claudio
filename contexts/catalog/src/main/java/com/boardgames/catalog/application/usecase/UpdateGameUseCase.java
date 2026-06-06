package com.boardgames.catalog.application.usecase;

import com.boardgames.catalog.application.command.UpdateGameCommand;
import com.boardgames.catalog.domain.Game;
import com.boardgames.catalog.domain.GameId;
import com.boardgames.catalog.domain.GameNotFoundException;
import com.boardgames.catalog.domain.GameRepository;
import com.boardgames.catalog.domain.Money;
import com.boardgames.catalog.domain.PlayerCount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateGameUseCase {

    private final GameRepository repository;

    public UpdateGameUseCase(GameRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(GameId id, UpdateGameCommand command) {
        Game game = repository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        game.updateDetails(
                command.title(),
                command.designer(),
                PlayerCount.of(command.minPlayers(), command.maxPlayers()),
                command.minAge(),
                command.durationMinutes(),
                Money.of(command.priceAmount(), command.priceCurrency())
        );

        repository.save(game);
    }
}

package com.boardgames.catalog.application.usecase;

import com.boardgames.catalog.domain.Game;
import com.boardgames.catalog.domain.GameId;
import com.boardgames.catalog.domain.GameNotFoundException;
import com.boardgames.catalog.domain.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetGameUseCase {

    private final GameRepository repository;

    public GetGameUseCase(GameRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Game execute(GameId id) {
        return repository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }
}

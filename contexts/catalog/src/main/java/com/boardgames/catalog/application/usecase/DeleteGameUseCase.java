package com.boardgames.catalog.application.usecase;

import com.boardgames.catalog.domain.GameId;
import com.boardgames.catalog.domain.GameNotFoundException;
import com.boardgames.catalog.domain.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteGameUseCase {

    private final GameRepository repository;

    public DeleteGameUseCase(GameRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(GameId id) {
        if (!repository.deleteById(id)) {
            throw new GameNotFoundException(id);
        }
    }
}

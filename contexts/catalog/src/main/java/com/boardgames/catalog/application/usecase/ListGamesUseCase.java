package com.boardgames.catalog.application.usecase;

import com.boardgames.catalog.domain.Game;
import com.boardgames.catalog.domain.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListGamesUseCase {

    private final GameRepository repository;

    public ListGamesUseCase(GameRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Game> execute() {
        return repository.findAll();
    }
}

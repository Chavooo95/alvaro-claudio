package com.boardgames.catalog.domain;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port. Implementations live in {@code infrastructure}.
 * The domain owns this contract: persistence is a detail.
 */
public interface GameRepository {

    void save(Game game);

    Optional<Game> findById(GameId id);

    List<Game> findAll();

    boolean deleteById(GameId id);
}

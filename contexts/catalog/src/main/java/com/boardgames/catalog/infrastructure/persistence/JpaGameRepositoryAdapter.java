package com.boardgames.catalog.infrastructure.persistence;

import com.boardgames.catalog.domain.Game;
import com.boardgames.catalog.domain.GameId;
import com.boardgames.catalog.domain.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaGameRepositoryAdapter implements GameRepository {

    private final SpringDataGameRepository jpaRepository;

    JpaGameRepositoryAdapter(SpringDataGameRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Game game) {
        jpaRepository.save(GameJpaMapper.toEntity(game));
    }

    @Override
    public Optional<Game> findById(GameId id) {
        return jpaRepository.findById(id.value()).map(GameJpaMapper::toDomain);
    }

    @Override
    public List<Game> findAll() {
        return jpaRepository.findAll().stream().map(GameJpaMapper::toDomain).toList();
    }

    @Override
    public boolean deleteById(GameId id) {
        if (!jpaRepository.existsById(id.value())) {
            return false;
        }
        jpaRepository.deleteById(id.value());
        return true;
    }
}

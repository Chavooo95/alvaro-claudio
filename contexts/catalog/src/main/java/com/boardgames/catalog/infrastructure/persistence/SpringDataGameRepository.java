package com.boardgames.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataGameRepository extends JpaRepository<GameJpaEntity, UUID> {
}

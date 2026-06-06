package com.boardgames.catalog.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "games")
class GameJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "designer", nullable = false)
    private String designer;

    @Column(name = "min_players", nullable = false)
    private int minPlayers;

    @Column(name = "max_players", nullable = false)
    private int maxPlayers;

    @Column(name = "min_age", nullable = false)
    private int minAge;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "price_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency;

    protected GameJpaEntity() {
    }

    GameJpaEntity(UUID id, String title, String designer, int minPlayers, int maxPlayers,
                  int minAge, int durationMinutes, BigDecimal priceAmount, String priceCurrency) {
        this.id = id;
        this.title = title;
        this.designer = designer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minAge = minAge;
        this.durationMinutes = durationMinutes;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
    }

    UUID getId() { return id; }
    String getTitle() { return title; }
    String getDesigner() { return designer; }
    int getMinPlayers() { return minPlayers; }
    int getMaxPlayers() { return maxPlayers; }
    int getMinAge() { return minAge; }
    int getDurationMinutes() { return durationMinutes; }
    BigDecimal getPriceAmount() { return priceAmount; }
    String getPriceCurrency() { return priceCurrency; }
}

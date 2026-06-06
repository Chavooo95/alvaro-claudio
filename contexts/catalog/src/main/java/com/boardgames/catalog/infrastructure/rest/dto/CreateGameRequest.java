package com.boardgames.catalog.infrastructure.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateGameRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 255) String designer,
        @Min(1) int minPlayers,
        @Min(1) int maxPlayers,
        @PositiveOrZero int minAge,
        @Positive int durationMinutes,
        @NotNull @PositiveOrZero BigDecimal priceAmount,
        @NotBlank @Size(min = 3, max = 3) String priceCurrency
) {
}

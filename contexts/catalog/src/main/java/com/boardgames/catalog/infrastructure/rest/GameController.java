package com.boardgames.catalog.infrastructure.rest;

import com.boardgames.catalog.application.command.CreateGameCommand;
import com.boardgames.catalog.application.command.UpdateGameCommand;
import com.boardgames.catalog.application.usecase.CreateGameUseCase;
import com.boardgames.catalog.application.usecase.DeleteGameUseCase;
import com.boardgames.catalog.application.usecase.GetGameUseCase;
import com.boardgames.catalog.application.usecase.ListGamesUseCase;
import com.boardgames.catalog.application.usecase.UpdateGameUseCase;
import com.boardgames.catalog.domain.GameId;
import com.boardgames.catalog.infrastructure.rest.dto.CreateGameRequest;
import com.boardgames.catalog.infrastructure.rest.dto.GameResponse;
import com.boardgames.catalog.infrastructure.rest.dto.UpdateGameRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/games")
class GameController {

    private final CreateGameUseCase createGame;
    private final GetGameUseCase getGame;
    private final ListGamesUseCase listGames;
    private final UpdateGameUseCase updateGame;
    private final DeleteGameUseCase deleteGame;

    GameController(CreateGameUseCase createGame,
                   GetGameUseCase getGame,
                   ListGamesUseCase listGames,
                   UpdateGameUseCase updateGame,
                   DeleteGameUseCase deleteGame) {
        this.createGame = createGame;
        this.getGame = getGame;
        this.listGames = listGames;
        this.updateGame = updateGame;
        this.deleteGame = deleteGame;
    }

    @PostMapping
    ResponseEntity<GameResponse> create(@Valid @RequestBody CreateGameRequest body) {
        GameId id = createGame.execute(new CreateGameCommand(
                body.title(), body.designer(),
                body.minPlayers(), body.maxPlayers(),
                body.minAge(), body.durationMinutes(),
                body.priceAmount(), body.priceCurrency()
        ));
        GameResponse response = GameResponse.from(getGame.execute(id));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id.value())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    GameResponse findOne(@PathVariable String id) {
        return GameResponse.from(getGame.execute(GameId.of(id)));
    }

    @GetMapping
    List<GameResponse> findAll() {
        return listGames.execute().stream().map(GameResponse::from).toList();
    }

    @PutMapping("/{id}")
    GameResponse update(@PathVariable String id, @Valid @RequestBody UpdateGameRequest body) {
        GameId gameId = GameId.of(id);
        updateGame.execute(gameId, new UpdateGameCommand(
                body.title(), body.designer(),
                body.minPlayers(), body.maxPlayers(),
                body.minAge(), body.durationMinutes(),
                body.priceAmount(), body.priceCurrency()
        ));
        return GameResponse.from(getGame.execute(gameId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String id) {
        deleteGame.execute(GameId.of(id));
    }
}

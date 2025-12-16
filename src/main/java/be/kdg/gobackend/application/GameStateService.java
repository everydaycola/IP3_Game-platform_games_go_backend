package be.kdg.gobackend.application;

import be.kdg.gobackend.api.dto.GameStateDto;
import be.kdg.gobackend.domain.exception.NotFoundException;
import be.kdg.gobackend.domain.game.GameState;
import be.kdg.gobackend.domain.game.GameStateId;
import be.kdg.gobackend.domain.game.Stone;
import be.kdg.gobackend.domain.player.PlayerId;
import be.kdg.gobackend.domain.repository.AiCatalog;
import be.kdg.gobackend.domain.repository.GameStateRepository;
import be.kdg.gobackend.infrastructure.gamestate.ai.dtos.AiRequestBodyDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service @Transactional @Slf4j public class GameStateService {

    private final GameStateRepository gameStateRepository;
    private final AiCatalog aiCatalog;

    public GameStateService(GameStateRepository gameStateRepository, AiCatalog aiCatalog) {
        this.gameStateRepository = gameStateRepository;
        this.aiCatalog = aiCatalog;
    }

    public GameState getState(GameStateId stateId, PlayerId playerId) {
        log.info("getting the state with id {}", stateId);
        final var gameState = gameStateRepository.get(stateId).orElseThrow(stateId::notFound);
        gameState.verifyPlayer(playerId);
        return gameState;
    }

    public GameState start(PlayerId playerId, int size) {
        log.info("player {} starting a game of size {}", playerId, size);
        final var gameState = new GameState(size, playerId);
        gameStateRepository.save(gameState);
        return gameState;
    }

    public GameState placeStone(GameStateId stateId, int x, int y, PlayerId playerId) {
        log.info("player in match {} placing a stone at {}, {}", stateId, x, y);
        final var gameState = gameStateRepository.get(stateId).orElseThrow(stateId::notFound);
        gameState.verifyPlayer(playerId);
        gameState.placeStone(x, y, Stone.BLACK);
        gameStateRepository.save(gameState);
        return gameState;
    }

    public GameState letAiPlaceStone(GameStateId stateId, PlayerId playerId) {
        log.info("ai placing a stone in match {}", stateId);
        final var gameState = gameStateRepository.get(stateId).orElseThrow(stateId::notFound);
        final var aiResponse = aiCatalog.askForMove(AiRequestBodyDto.from(gameState))
                                        .orElseThrow(() -> new IllegalStateException("Ai could not make a move"));
        gameState.verifyPlayer(playerId);
        log.info("ai chose row {}, col {}, move {}", aiResponse.row(), aiResponse.col(), aiResponse.best_move());
        if (aiResponse.col() == -1 && aiResponse.row() == -1) {
            gameState.passTurn(Stone.WHITE);
        } else {
            gameState.placeStone(aiResponse.row(), aiResponse.col(), Stone.WHITE);
        }
        gameStateRepository.save(gameState);
        return gameState;
    }

    public GameState passTurn(GameStateId stateId, PlayerId playerId) {
        log.info("passing turn to player in match {}", stateId);
        final var gameState = gameStateRepository.get(stateId).orElseThrow(stateId::notFound);
        gameState.verifyPlayer(playerId);
        gameState.passTurn(Stone.BLACK);
        gameStateRepository.save(gameState);
        return gameState;
    }

    public GameState getPlayingStateForPlayerAndState(PlayerId playerId, int size) {
        log.info("getting the state for player {} and size {}", playerId, size);
        return gameStateRepository.getPlayingGameForPlayerAndSize(playerId, size)
                .orElseThrow(() -> new NotFoundException("No game found for player " + playerId.id()));
    }
}

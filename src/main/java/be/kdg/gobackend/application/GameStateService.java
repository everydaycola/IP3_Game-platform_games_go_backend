package be.kdg.gobackend.application;

import be.kdg.gobackend.domain.game.GameState;
import be.kdg.gobackend.domain.game.GameStateId;
import be.kdg.gobackend.domain.game.Stone;
import be.kdg.gobackend.domain.player.PlayerId;
import be.kdg.gobackend.domain.repository.GameStateRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class GameStateService {

    private final GameStateRepository gameStateRepository;

    public GameStateService(GameStateRepository gameStateRepository) {
        this.gameStateRepository = gameStateRepository;
    }

    public GameState getState(GameStateId stateId) {
        log.info("getting the state with id {}", stateId);
        return gameStateRepository.get(stateId).orElseThrow(stateId::notFound);
    }

    public GameState start(PlayerId playerId, int size) {
        log.info("player {} starting a game of size {}", playerId, size);
        final var gameState = new GameState(size, playerId);
        gameStateRepository.save(gameState);
        return gameState;
    }

    public GameState placeStone(GameStateId stateId, int x, int y) {
        log.info("player in match {} placing a stone at {}, {}", stateId, x, y);
        final var gameState = gameStateRepository.get(stateId).orElseThrow(stateId::notFound);
        gameState.placeStone(x, y, Stone.BLACK);
        gameStateRepository.save(gameState);
        return gameState;
    }
}

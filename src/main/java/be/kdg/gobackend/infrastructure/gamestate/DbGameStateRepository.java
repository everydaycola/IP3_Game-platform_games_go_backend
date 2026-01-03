package be.kdg.gobackend.infrastructure.gamestate;

import be.kdg.gobackend.domain.game.GameState;
import be.kdg.gobackend.domain.game.GameStateId;
import be.kdg.gobackend.domain.game.Stone;
import be.kdg.gobackend.domain.player.PlayerId;
import be.kdg.gobackend.domain.repository.GameStateRepository;
import be.kdg.gobackend.infrastructure.gamestate.jpa.JpaGameStateEntity;
import be.kdg.gobackend.infrastructure.gamestate.jpa.JpaGameStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbGameStateRepository implements GameStateRepository {

    final JpaGameStateRepository gameStateRepository;

    @Override
    public void save(GameState gameState) {
        log.info("Saving game state to database");
        gameStateRepository.save(JpaGameStateEntity.fromDomain(gameState));
    }

    @Override public Optional<GameState> get(GameStateId stateId) {
        log.info("Getting game state from database");
        return gameStateRepository.findById(stateId.id()).map(JpaGameStateEntity::toDomain);
    }

    @Override
    public Optional<GameState> getPlayingGameForPlayer(PlayerId playerId) {
        log.info("Getting game for player {}  from database", playerId.id());
        return gameStateRepository.findFirstByPlayer1OrPlayer2AndWinner(playerId.id(), playerId.id(), Stone.EMPTY).map(JpaGameStateEntity::toDomain);
    }

    @Override
    public Optional<GameState> getOngoingAiGameForPlayer(PlayerId playerId) {
        log.info("Searching if player {} has non finished AI games", playerId.id());
        return gameStateRepository.findByPlayer1AndIsAiGameAndWinner(playerId.id(), true, Stone.EMPTY).map(JpaGameStateEntity::toDomain);
    }

    @Override
    public void removeGame(GameState notFinishedAiGame) {
        log.info("Found a not finished AI game for a player, removing this for startup of new ai game");
        gameStateRepository.delete(JpaGameStateEntity.fromDomain(notFinishedAiGame));
    }
}

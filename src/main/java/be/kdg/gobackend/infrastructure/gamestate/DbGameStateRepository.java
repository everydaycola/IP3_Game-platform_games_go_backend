package be.kdg.gobackend.infrastructure.gamestate;

import be.kdg.gobackend.domain.game.GameState;
import be.kdg.gobackend.domain.game.GameStateId;
import be.kdg.gobackend.domain.player.PlayerId;
import be.kdg.gobackend.domain.repository.GameStateRepository;
import be.kdg.gobackend.infrastructure.gamestate.jpa.JpaGameStateEntity;
import be.kdg.gobackend.infrastructure.gamestate.jpa.JpaGameStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class DbGameStateRepository implements GameStateRepository {

    final JpaGameStateRepository gameStateRepository;

    public DbGameStateRepository(JpaGameStateRepository gameStateRepository) {
        this.gameStateRepository = gameStateRepository;
    }

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
    public Optional<GameState> getGameForPlayer(PlayerId playerId) {
        log.info("Getting game for player from database");
        return gameStateRepository.findByPlayer(playerId.id()).map(JpaGameStateEntity::toDomain);
    }
}

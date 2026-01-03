package be.kdg.gobackend.domain.repository;

import be.kdg.gobackend.domain.game.GameState;
import be.kdg.gobackend.domain.game.GameStateId;
import be.kdg.gobackend.domain.player.PlayerId;

import java.util.Optional;

public interface GameStateRepository {
    void save(GameState gameState);
    Optional<GameState> get(GameStateId id);
    Optional<GameState> getPlayingGameForPlayer(PlayerId playerId);
}

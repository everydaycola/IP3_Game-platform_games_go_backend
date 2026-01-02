package be.kdg.gobackend.infrastructure.gamestate.jpa;

import be.kdg.gobackend.domain.game.Stone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaGameStateRepository extends JpaRepository<JpaGameStateEntity, UUID> {
    Optional<JpaGameStateEntity> findFirstByPlayer1AndWinnerAndBoardSize(UUID player, Stone winner, int boardSize);
}

package be.kdg.gobackend.infrastructure.gamestate.jpa;

import be.kdg.gobackend.domain.game.GameState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaGameStateRepository extends JpaRepository<JpaGameStateEntity, UUID> {
    Optional<JpaGameStateEntity> findByPlayer(UUID id);
}

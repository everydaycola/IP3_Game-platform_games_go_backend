package be.kdg.gobackend.infrastructure.gamestate.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaGameStateRepository extends JpaRepository<JpaGameStateEntity, UUID> {
    Optional<JpaGameStateEntity> findFirstByPlayer1OrPlayer2(UUID player1, UUID player2);
}

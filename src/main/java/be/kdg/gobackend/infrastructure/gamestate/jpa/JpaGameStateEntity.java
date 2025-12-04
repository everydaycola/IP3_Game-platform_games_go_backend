package be.kdg.gobackend.infrastructure.gamestate.jpa;

import be.kdg.gobackend.domain.game.Board;
import be.kdg.gobackend.domain.game.GameState;
import be.kdg.gobackend.domain.game.GameStateId;
import be.kdg.gobackend.domain.game.Stone;
import be.kdg.gobackend.domain.player.PlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "game_state")
public class JpaGameStateEntity {
    @Id
    private UUID id;

    @Column(length = 361) @Convert(converter = BoardConverter.class)
    private Stone[] board;

    @Column
    private int size;

    @Column
    private UUID player;

    @Column
    private boolean atTurn;

    public static JpaGameStateEntity fromDomain(GameState gameState) {
        return new JpaGameStateEntity(
                gameState.getId().id(),
                gameState.getBoard().getStones(),
                gameState.getBoard().getSize(),
                gameState.getPlayer().id(),
                gameState.isAtTurn()
        );
    }

    public GameState toDomain() {
        return new GameState(
                new GameStateId(this.id),
                new Board(this.size, this.board),
                new PlayerId(this.player),
                this.atTurn
        );
    }
}

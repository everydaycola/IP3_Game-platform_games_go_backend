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
    private int boardSize;

    @Column
    private double score;

    @Column
    private UUID player;

    @Column
    private boolean atTurn;

    @Column
    private boolean isLastTurnPassed;

    @Column
    private Stone winner;

    public static JpaGameStateEntity fromDomain(GameState gameState) {
        return new JpaGameStateEntity(
                gameState.getId().id(),
                gameState.getBoard().getStones(),
                gameState.getBoard().getSize(),
                gameState.getScore(),
                gameState.getPlayer().id(),
                gameState.isAtTurn(),
                gameState.isLastTurnPassed(),
                gameState.getWinner()
        );
    }

    public GameState toDomain() {
        return new GameState(
                new GameStateId(this.id),
                new Board(this.boardSize, this.board),
                new PlayerId(this.player),
                this.atTurn,
                this.isLastTurnPassed,
                this.score,
                this.winner
        );
    }
}

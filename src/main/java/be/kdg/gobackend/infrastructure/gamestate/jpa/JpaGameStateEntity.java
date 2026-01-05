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

import java.time.LocalDateTime;
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
    private boolean isAiGame;

    @Column
    private int boardSize;

    @Column
    private double score;

    @Column
    private UUID player1;

    @Column
    private UUID player2;

    @Column
    private boolean player1AtTurn;

    @Column
    private boolean isLastTurnPassed;

    @Column
    private Stone winner;

    @Column
    private LocalDateTime createdAt;

    public static JpaGameStateEntity fromDomain(GameState gameState) {
        return new JpaGameStateEntity(
                gameState.getId().id(),
                gameState.getBoard().getStones(),
                gameState.isAiGame(),
                gameState.getBoard().getSize(),
                gameState.getScore(),
                gameState.getPlayer1().id(),
                gameState.getPlayer2().id(),
                gameState.isPlayer1AtTurn(),
                gameState.isLastTurnPassed(),
                gameState.getWinner(),
                gameState.getCreatedAt()
        );
    }

    public GameState toDomain() {
        return new GameState(
                new GameStateId(this.id),
                new Board(this.boardSize, this.board),
                this.isAiGame,
                new PlayerId(this.player1),
                new PlayerId(this.player2),
                this.player1AtTurn,
                this.isLastTurnPassed,
                this.score,
                this.winner,
                this.createdAt
        );
    }
}

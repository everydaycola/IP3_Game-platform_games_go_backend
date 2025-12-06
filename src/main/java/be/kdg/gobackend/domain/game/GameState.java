package be.kdg.gobackend.domain.game;

import be.kdg.gobackend.domain.player.PlayerId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

// note in general, the game only allows user vs AI at the moment.
@Getter
@Slf4j
@AllArgsConstructor
public class GameState {
    private static final double KOMI = 6.5;
    private final GameStateId id;
    private final Board board;
    private final PlayerId player;
    private boolean atTurn;
    private boolean isLastTurnPassed;
    private double score;
    private Stone winner;

    public GameState(int size, PlayerId player) {
        log.info("Creating a new game with player {} of size {}", player, size);
        this.player = player;
        this.id = new GameStateId();
        this.board = new Board(size);
        // the player always starts and is always at turn first (thus playing black)
        this.atTurn = true;
        this.isLastTurnPassed = false;
        this.score = -KOMI;
        this.winner = Stone.EMPTY;
    }

    public String[][] getBoardForDto() {
        return this.board.getBoardForDto();
    }

    public void placeStone(int x, int y, Stone stone) {
        log.info("placing a stone at {}, {}", x, y);
        final var playerTurn = stone.equals(Stone.BLACK);
        if (atTurn != playerTurn) throw new IllegalStateException("It is not player's turn");
        board.placeStone(x, y, stone);
        this.isLastTurnPassed = false;
        this.atTurn = !playerTurn;
    }

    public int getSize() {
        return this.board.getSize();
    }

    public void passTurn() {
        log.info("Passing turn");
        if (this.isLastTurnPassed) {
            log.info("The game has ended, calculating score");
            this.score = this.board.calculateScore();
            log.info("Final score: {}", this.score);
            this.winner = this.score > 0 ? Stone.BLACK : Stone.WHITE;
            log.info("Winner: {}", this.winner);
        }
        this.isLastTurnPassed = true;
        this.atTurn = !this.atTurn;
    }
}

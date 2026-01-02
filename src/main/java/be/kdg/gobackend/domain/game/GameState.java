package be.kdg.gobackend.domain.game;

import be.kdg.gobackend.domain.exception.NotFoundException;
import be.kdg.gobackend.domain.player.PlayerId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

// note in general, the game only allows user vs AI at the moment.
@Getter @Slf4j @AllArgsConstructor public class GameState {
    private static final double KOMI = 6.5;
    private final GameStateId id;
    private final Board board;
    private final boolean isAiGame;
    private final PlayerId player1;
    private final PlayerId player2;
    private boolean player1AtTurn;
    private boolean isLastTurnPassed;
    private double score;
    private Stone winner;

    public GameState(int size, PlayerId player1,PlayerId player2, boolean isAiGame) {
        log.info("Creating a new game with player {} of size {}", player1, size);
        this.player1 = player1;
        this.player2 = player2;
        this.id = new GameStateId();
        this.board = new Board(size);
        this.isAiGame = isAiGame;
        // the player always starts and is always at turn first (thus playing black)
        this.player1AtTurn = true;
        this.isLastTurnPassed = false;
        this.score = 0.0;
        this.winner = Stone.EMPTY;
    }

    public String[][] getBoardForDto() {
        return this.board.getBoardForDto();
    }

    public void placeStone(int x, int y, Stone stone) {
        log.info("placing a stone at {}, {}", x, y);
        final var playerTurn = stone.equals(Stone.BLACK);
        if (player1AtTurn != playerTurn) throw new IllegalStateException("It is not player's turn");
        board.placeStone(x, y, stone);
        this.isLastTurnPassed = false;
        this.player1AtTurn = !playerTurn;
    }

    public int getSize() {
        return this.board.getSize();
    }

    public void passTurn(Stone stone) {
        final var playerTurn = stone.equals(Stone.BLACK);
        if (player1AtTurn != playerTurn) throw new IllegalStateException("It is not player's turn");
        log.info("Passing turn");
        if (!this.isLastTurnPassed) {
            this.isLastTurnPassed = true;
            this.player1AtTurn = !this.player1AtTurn;
            return;
        }
        this.score = this.board.calculateScore() - KOMI;
        this.winner = this.score > 0 ? Stone.BLACK : Stone.WHITE;
        log.info("""
                             The game has ended.
                             Final score: {}
                             Winner: {}
                             """, this.score, this.winner);

    }

    public void verifyPlayer(PlayerId playerId) {
        if (!player1.equals(playerId)) throw new NotFoundException("Game was not found");
    }
}

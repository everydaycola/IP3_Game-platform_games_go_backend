package be.kdg.gobackend.domain.game;

import be.kdg.gobackend.domain.player.PlayerId;
import lombok.AllArgsConstructor;
import lombok.Getter;

// note in general, the game only allows user vs AI at the moment.
@Getter
@AllArgsConstructor
public class GameState {
    private final GameStateId id;
    private final Board board;
    private final PlayerId player;
    private boolean atTurn;

    public GameState(int size, PlayerId player) {
        this.player = player;
        this.id = new GameStateId();
        this.board = new Board(size);
        // the player always starts and is always at turn first (thus playing black)
        this.atTurn = true;
    }

    public String[][] getBoardForDto() {
        return this.board.getBoardForDto();
    }

    public void placeStone(int x, int y, Stone stone) {
        final var playerTurn = stone.equals(Stone.BLACK);
        if (atTurn != playerTurn) throw new IllegalStateException("It is not player's turn");
        board.placeStone(x, y, stone);
        this.atTurn = !playerTurn;
    }

    public int getSize() {
        return this.board.getSize();
    }

//    public Stone getStone(int x, int y) {
//        return board.getStone(x, y);
//    }
}

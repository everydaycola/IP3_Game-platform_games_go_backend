package be.kdg.gobackend.domain.game;

import be.kdg.gobackend.domain.player.PlayerId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameState {
    private final GameStateId id;
    private final Board board;
    private final PlayerId player;

    public GameState(int size, PlayerId player) {
        this.player = player;
        this.id = new GameStateId();
        this.board = new Board(size);
    }
}

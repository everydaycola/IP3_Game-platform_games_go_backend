package be.kdg.gobackend.domain.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public class Board {
    private final int size;
    private final Stone[] stones;

    public Board(int size) {
        if (size < 5 || size > 19) throw new IllegalArgumentException("Board size must be between 5 and 19");
        this.size = size;
        this.stones = new Stone[size * size];
        Arrays.fill(this.stones, Stone.EMPTY);
    }
    
    public String[][] getBoardForDto() {
        String[][] board = new String[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                board[y][x] = String.valueOf(getStone(x, y).getShortName());
            }
        }
        return board;
    }

    public void placeStone(int x, int y, Stone stone) {
        if (0 >= x || x >= size || 0 >= y || y >= size)
            throw new IllegalArgumentException("Position is out of bounds");
        if (getStone(x, y) != Stone.EMPTY)
            throw new IllegalStateException("Spot is already taken");
        stones[y * size + x] = stone;
    }

    private Stone getStone(int x, int y) {
        return stones[y * size + x];
    }
}
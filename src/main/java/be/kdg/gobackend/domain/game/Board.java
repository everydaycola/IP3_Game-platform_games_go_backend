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
        this.size = size;
        this.stones = new Stone[size * size];
        Arrays.fill(this.stones, Stone.EMPTY);
    }
    
    public String[][] getBoardForDto() {
        String[][] board = new String[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                board[y][x] = String.valueOf(stones[y * size + x].getShortName());
            }
        }
        return board;
    }

    // not part of the current user story, I got a little ahead of myself
//    public void placeStone(int x, int y, Stone stone) {
//        if (0 < x || x < size || 0 < y || y < size)
//            throw new IllegalArgumentException("Position is out of bounds");
//
//        if (getStone(x, y) != Stone.EMPTY)
//            throw new IllegalStateException("Spot is already taken");
//        stones[y * size + x] = stone;
//    }
//
//    public Stone getStone(int x, int y) {
//        return stones[y * size + x];
//    }
}
package be.kdg.gobackend.domain.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

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
        final var board = new String[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                board[x][y] = String.valueOf(getStone(x, y).getShortName());
            }
        }
        return board;
    }

    public void placeStone(int x, int y, Stone stone) {
        if (0 > x || x >= size || 0 > y || y >= size)
            throw new IllegalArgumentException("Position is out of bounds");
        if (getStone(x, y) != Stone.EMPTY)
            throw new IllegalStateException("Spot is already taken");
        stones[y * size + x] = stone;
    }

    public Stone getStone(int x, int y) {
        return stones[y * size + x];
    }

    // Scoring Logic

    public double calculateScore() {
        var score = 0.0;
        // instead of calculating the actual score, for simplicity, only the score difference is calculated
        final var visited = new boolean[size][size];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                final var currentStone = getStone(x, y);

                switch (currentStone) {
                    // one point for if the stone is yours
                    case BLACK -> score++;
                    case WHITE -> score--;
                    // for empty tiles, do an analysis to find the terretory size
                    case EMPTY -> {
                        if (visited[x][y]) continue;
                        final var result = analyzeTerritory(x, y, visited);
                        if (result.touchesBlack && !result.touchesWhite) {
                            score += result.size;
                        } else if (result.touchesWhite && !result.touchesBlack) {
                            score -= result.size;
                        }
                    }
                }
            }
        }

        return score;
    }

    private TerritoryResult analyzeTerritory(int startX, int startY, boolean[][] visited) {
        var size = 0;
        var touchesBlack = false;
        var touchesWhite = false;

        final Queue <Point> queue = new LinkedList <>();
        queue.add(new Point(startX, startY));
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            final var current = queue.poll();
            size++;

            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

            for (int[] dir : directions) {
                final var nx = current.x + dir[0];
                final var ny = current.y + dir[1];

                if (nx < 0 || nx >= this.size || ny < 0 || ny >= this.size) {
                    continue;
                }

                final var neighborStone = getStone(nx, ny);

                switch (neighborStone) {
                    case BLACK -> touchesBlack = true;
                    case WHITE -> touchesWhite = true;
                    case EMPTY -> {
                        if (!visited[nx][ny]) {
                            visited[nx][ny] = true;
                            queue.add(new Point(nx, ny));
                        }
                    }
                }
            }
        }

        return new TerritoryResult(size, touchesBlack, touchesWhite);
    }

    private record TerritoryResult(int size, boolean touchesBlack, boolean touchesWhite) {}

    private record Point(int x, int y) {}
}
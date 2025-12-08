package be.kdg.gobackend.api.dto;

import be.kdg.gobackend.domain.game.GameState;

import java.util.UUID;

public record GameStateDto(
        UUID id,
        String[][] board,
        int size,
        String winner,
        double score,
        boolean atTurn,
        boolean isLastTurnPassed

) {
    public static GameStateDto from(GameState gameState) {
        return new GameStateDto(
                gameState.getId().id(),
                gameState.getBoardForDto(),
                gameState.getSize(),
                gameState.getWinner().toString(),
                gameState.getScore(),
                gameState.isAtTurn(),
                gameState.isLastTurnPassed()
        );
    }
}
